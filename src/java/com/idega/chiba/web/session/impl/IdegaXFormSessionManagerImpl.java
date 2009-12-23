package com.idega.chiba.web.session.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.chiba.web.IWBundleStarter;
import org.chiba.web.WebAdapter;
import org.chiba.web.WebFactory;
import org.chiba.web.session.XFormsSession;
import org.chiba.web.session.XFormsSessionManager;
import org.chiba.xml.xforms.config.Config;
import org.chiba.xml.xforms.exception.XFormsException;
import org.jdom.Attribute;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.idega.core.cache.IWCacheManager2;
import com.idega.event.HttpSessionDestroyed;
import com.idega.event.IWHttpSessionsManager;
import com.idega.event.PDFGeneratedEvent;
import com.idega.event.SessionPollerEvent;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.xml.XmlUtil;

/**
 * Idega implementation of a XFormsSessionManager.
 *
 * @author Anton Makarov</a>
 */

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class IdegaXFormSessionManagerImpl implements XFormsSessionManager, ApplicationListener {	
    
	private static final Logger LOGGER = Logger.getLogger(IdegaXFormSessionManagerImpl.class.getName());
	
	private static final String XFORMS_SESSIONS_CACHE_NAME = "idegaXFormsSessionsCache";
	
	private static XFormsSessionManager instance = null;
	
	@Autowired
	private IWHttpSessionsManager httpSessionsManager;
	
	private int wipingInterval;
	private static int maxSessions;
	private int timeOut;
	
    public static XFormsSessionManager getXFormsSessionManager() {
    	if (instance == null) {
    		new IdegaXFormSessionManagerImpl();
        }
        return instance;
    }
    
    private IdegaXFormSessionManagerImpl() {
    	instance = this;
    }
    
    public synchronized XFormsSession createXFormsSession(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws XFormsException {
    	try {
         	Config config = IWBundleStarter.webFactory.getConfig();
        	String mode = config.getProperty("scripted");
        	
        	if(mode == null)
            	mode = "true";
            
            if (mode.equalsIgnoreCase("true")){
                request.setAttribute(WebFactory.SCRIPTED,"true");
            }else if(mode.equalsIgnoreCase("false")){
                request.setAttribute(WebFactory.SCRIPTED,"false");
            }
        } catch(Exception e) {
        	LOGGER.log(Level.WARNING, "Error parsing configuration for XForm session", e);
        	CoreUtil.sendExceptionNotification(e);
        }
    	
        XFormsSession xFormsSessionBase = new IdegaXFormsSessionBase(request,response,session);
        if (LOGGER.isLoggable(Level.INFO)) {
        	LOGGER.info("Created XFormsSession: '" + xFormsSessionBase.getKey() + "'");
        }
        return xFormsSessionBase;
    }

    /**
     * Register XFormSession.
     */
    public void addXFormsSession(XFormsSession xfSession) {
    	if (xfSession == null) {
    		LOGGER.warning("Tried to add XForms session, but it is null!");
    		return;
    	}
    	
    	Map<String, XFormsSession> sessionXForms = getCurrentSessionXForms();
    	if (sessionXForms == null) {
    		LOGGER.warning("XForms sessions cache is null! Unable to put XForms session: " + xfSession.getKey());
    		return;
    	}

        sessionXForms.put(xfSession.getKey(), xfSession);
    }

    /**
     * Remove XFormsSession from the cache.
     *
     * @param id the XFormsSession id
     */
    public void deleteXFormsSession(String id) {
    	if (StringUtil.isEmpty(id)) {
    		LOGGER.warning("Session id is not provided! It's empty or null: '" + id + "'");
    		return;
    	}
    	
    	Map<String, XFormsSession> sessionXForms = getCurrentSessionXForms();
    	if (sessionXForms == null) {
    		LOGGER.warning("XForms sessions cache is null! Unable to remove XForms session from cache: '" + id + "'");
    		return;
    	}
    	
    	XFormsSession removed = sessionXForms.remove(id);
        if (removed == null) {
        	LOGGER.warning("Unable to remove XForms session from SessionManager: '" + id + "'. Element by this id exists in cache: " +
        			sessionXForms.containsKey(id));
        } else {
        	LOGGER.info("Deleted XForms session from SessionManager: '" + id + "'");
        }
    }

    public void destroy() {}

    /**
     * fetches a XFormsSession by its id
     *
     * @param id the id of the session as created during createXFormsSession() call
     * @return returns the XFormsSession object associated with given id or null if object does not exist
     */
    public XFormsSession getXFormsSession(String id) {
    	if (StringUtil.isEmpty(id)) {
    		LOGGER.warning("Session id is not provided! It's empty or null: '" + id + "'");
    		return null;
    	}
    	
    	Map<String, XFormsSession> forms = getCurrentSessionXForms();
    	if (forms == null) {
    		LOGGER.warning("XForms sessions cache is null!");
    		return null;
    	}
    	
    	XFormsSession formsSession = forms.get(id);
        if (formsSession == null) {
        	LOGGER.warning("XForms session with '" + id + "' not found!");
            return null;
        }
        
        return formsSession;
    }

    public synchronized void init() {}
            
    /**
     * Get xforms in current from the cache.
     * 
     * @return map of [key,xform].
     */
	private Map<String, XFormsSession> getCurrentSessionXForms() {
		//	All intervals are in seconds
		long halfAnHour = 60 * 60 * 30;	
		long ttlIdle = wipingInterval == 0 ? halfAnHour : wipingInterval / 1000;	//	Set to 0 in configuration XML file
		long ttlCache = timeOut == 0 ? halfAnHour : timeOut / 1000;					//	Set to 2 hours in configuration XML file
		try {
			return IWCacheManager2.getInstance(IWMainApplication.getDefaultIWMainApplication()).getCache(XFORMS_SESSIONS_CACHE_NAME, getMaxSessions(),
					false, false, ttlIdle, ttlCache);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting cache of XForms (" + XFORMS_SESSIONS_CACHE_NAME + ")", e);
			CoreUtil.sendExceptionNotification(e);
		}
		
		return null;
	}
	
	public static final int getMaxSessions() {
		if (maxSessions == 0)
			return Integer.valueOf(IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty("max_xforms_sessions", String.valueOf(25)));
		
		return maxSessions;
	}

	public int getSessionCount() {
		Map<String, XFormsSession> sessions = getCurrentSessionXForms();
		return sessions == null ? -1 : sessions.size();
	}

	public void setInterval(int wipingInterval) {
		this.wipingInterval = wipingInterval;
	}

	public void setMaxSessions(int maxSessions) {
		IdegaXFormSessionManagerImpl.maxSessions = maxSessions;
	}

	public void setTimeout(int milliseconds) {
		timeOut = milliseconds;
	}

	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof SessionPollerEvent) {
			Set<String> ids = getKeysForXFormsSessions();
			try {
				if (ids == null || ids.isEmpty()) {
					return;
				}
				
				for (String id: ids) {
					XFormsSession session = getXFormsSession(id);
					if (session instanceof IdegaXFormsSessionBase) {
						if (getHttpSessionsManager().isSessionValid(((IdegaXFormsSessionBase) session).getHttpSessionId())) {
							//	Keeping session alive!
							session.updateLRU();
						} else {
							//	Removing XForms session because original HTTP session has expired
							invalidateXFormsSession(session, id);
						}
					}
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error while trying to keep alive XForms sessions: " + ids);
			} finally {
				getSessionCount();
			}
		} else if (event instanceof HttpSessionDestroyed) {
			String destroyedHttpSessionId = ((HttpSessionDestroyed) event).getHttpSessionId();
			invalidateXFormsSessions(destroyedHttpSessionId);
		} else if (event instanceof PDFGeneratedEvent) {
			invalidateXFormsSession(((PDFGeneratedEvent) event).getPdfSource());
		}
	}
	
	private void invalidateXFormsSession(Document doc) {
		if (doc == null) {
			return;
		}
		
		org.jdom.Document document = XmlUtil.getJDOMXMLDocument(doc);
		if (document == null) {
			return;
		}
		
		List<Element> forms = XmlUtil.getElementsByXPath(document, "form", XmlUtil.XHTML_NAMESPACE_ID);
		if (ListUtil.isEmpty(forms)) {
			return;
		}
		
		for (Element form: forms) {
			Attribute action = form.getAttribute("action");
			if (action == null) {
				break;
			}
			
			String actionValue = action.getValue();
			if (StringUtil.isEmpty(actionValue) || actionValue.indexOf(IWBundleStarter.SESSION_KEY) == -1) {
				break;
			}
			
			String sessionId = actionValue.substring(actionValue.indexOf(IWBundleStarter.SESSION_KEY) + IWBundleStarter.SESSION_KEY.length() + 1);
			invalidateXFormsSession(getXFormsSession(sessionId), sessionId);
		}
	}
	
	private Set<String> getKeysForXFormsSessions() {
		try {
			return getCurrentSessionXForms().keySet();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error while resolving keys for XForms sessions", e);
		}
		return null;
	}
	
	private void invalidateXFormsSessions(String httpSessionId) {
		if (StringUtil.isEmpty(httpSessionId)) {
			return;
		}
		
		Set<String> xFormsSessionsIds = getKeysForXFormsSessions();
		try {
			if (ListUtil.isEmpty(xFormsSessionsIds)) {
				return;
			}
			
			for (String xFormSessionId: xFormsSessionsIds) {
				XFormsSession session = getXFormsSession(xFormSessionId);
				if (session instanceof IdegaXFormsSessionBase) {
					if (httpSessionId.equals(((IdegaXFormsSessionBase) session).getHttpSessionId())) {
						invalidateXFormsSession(session, xFormSessionId);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error while trying invalidate XForms sessions tied to destroyed HttpSession: " + httpSessionId, e);
		}
	}
	
	private void invalidateXFormsSession(XFormsSession session, String id) {
		try {
			WebAdapter adapter = session.getAdapter();
	        if (adapter != null) {
	        	adapter.shutdown();
	        }
		} catch (Exception e) {
		} finally {
			deleteXFormsSession(id);
		}
	}

	public IWHttpSessionsManager getHttpSessionsManager() {
		return httpSessionsManager;
	}

	public void setHttpSessionsManager(IWHttpSessionsManager httpSessionsManager) {
		this.httpSessionsManager = httpSessionsManager;
	}
}