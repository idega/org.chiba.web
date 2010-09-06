package com.idega.chiba.web.session.impl;

import java.util.Date;
import java.util.Iterator;
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

import com.idega.chiba.ChibaUtils;
import com.idega.chiba.cache.XFormsSessionsCacheGuardian;
import com.idega.chiba.cache.XFormsSessionsCacheListener;
import com.idega.chiba.web.exception.IdegaChibaException;
import com.idega.core.cache.CacheMapGuardian;
import com.idega.core.cache.CacheMapListener;
import com.idega.core.cache.IWCacheManager2;
import com.idega.event.HttpSessionDestroyed;
import com.idega.event.IWHttpSessionsManager;
import com.idega.event.PDFGeneratedEvent;
import com.idega.event.SessionPollerEvent;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
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
	
	private CacheMapListener<String, XFormsSession> cacheListener;
	private CacheMapGuardian<String, XFormsSession> cacheGuardian;
	
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
        	
        	if (mode == null)
            	mode = Boolean.TRUE.toString();
            
            if (mode.equalsIgnoreCase(Boolean.TRUE.toString())) {
                request.setAttribute(WebFactory.SCRIPTED, Boolean.TRUE.toString());
            } else if (mode.equalsIgnoreCase(Boolean.FALSE.toString())) {
                request.setAttribute(WebFactory.SCRIPTED, Boolean.FALSE.toString());
            }
        } catch(Exception e) {
        	LOGGER.log(Level.WARNING, "Error parsing configuration for XForm session", e);
        	CoreUtil.sendExceptionNotification(e);
        }
    	
        XFormsSession xFormsSessionBase = new IdegaXFormsSessionBase(request, response, session);
        if (LOGGER.isLoggable(Level.INFO)) {
        	LOGGER.info("Created XFormsSession: '" + xFormsSessionBase.getKey() + "' for HTTP session: " + (session == null ? "unknown" : session.getId()));
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

    	int currentSize = sessionXForms.size();
    	int maxSize = getMaxSessions();
    	if (currentSize >= maxSize) {
    		String messageToTheUser = ChibaUtils.getInstance()
    			.getLocalizedString("xforms_limit_reached", "We are very sorry, the limit of max users was reached. Please, try a bit later.");
    		throw new IdegaChibaException("The limit was reached of XForms sessions! Able to store sessions: " + maxSize + ", now storing: " + currentSize,
    				messageToTheUser,
    				Boolean.TRUE);
    	}
    	
        sessionXForms.put(xfSession.getKey(), xfSession);
    }

    /**
     * Remove XFormsSession from the cache.
     *
     * @param id the XFormsSession id
     */
    public void deleteXFormsSession(String id) {
    	deleteXFormsSession(id, null);
    }
    
    public void deleteXFormsSession(String id, String explanation) {
    	if (StringUtil.isEmpty(id)) {
    		LOGGER.warning("XForm session id is not provided! It is empty or null: '" + id + "'");
    		return;
    	}
    	
    	Map<String, XFormsSession> sessionXForms = getCurrentSessionXForms();
    	if (sessionXForms == null) {
    		LOGGER.warning("XForms sessions cache is null! Unable to remove XForms session from cache: '" + id + "'");
    		return;
    	}
    	
    	XFormsSession removed = sessionXForms.remove(id);
        if (removed == null) {
        	LOGGER.warning("Unable to delete XForms session from SessionManager: '" + id + "'. Element by this id exists in cache: " +
        			sessionXForms.get(id));
        } else {
        	if (explanation == null) {
        		String testMessage = "TEST! Exploring XForm deletion traces";
        		try {
	        		if (IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("log_xform_deletion_trace", Boolean.FALSE)) {
	        			throw new RuntimeException(testMessage);
	        		}
        		} catch (Exception e) {
        			LOGGER.log(Level.INFO, testMessage, e);
        			CoreUtil.sendExceptionNotification(e);
        		}
        		explanation = "No explanation provided.";
        	}
        	LOGGER.info("Deleted XForms session: '" + id + "' for HTTP session: " + ChibaUtils.getInstance().getCurrentHttpSessionId() + ". " +
        			explanation);
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
    	
    	XFormsSession formsSession = null;
    	try {
    		formsSession = forms.get(id);
    	} catch (Exception e) {
    		LOGGER.log(Level.WARNING, "Error getting XForm session: " + id + " from: " + forms + ", active sessions: " + forms.keySet());
    	}
        if (formsSession == null) {
        	LOGGER.warning("XForms session with '" + id + "' not found!");
            return null;
        }
        
        return formsSession;
    }
    
    public synchronized void init() {}
    
    private CacheMapListener<String, XFormsSession> getCacheListener() {
    	if (cacheListener == null) {
    		cacheListener = new XFormsSessionsCacheListener<String, XFormsSession>();
    	}
    	return cacheListener;
    }
    
    private CacheMapGuardian<String, XFormsSession> getCacheGuardian() {
    	if (cacheGuardian == null) {
    		cacheGuardian = new XFormsSessionsCacheGuardian<String, XFormsSession>();
    	}
    	return cacheGuardian;
    }
    
    /**
     * Get xforms in current from the cache.
     * 
     * @return map of [key,xform].
     */
	private Map<String, XFormsSession> getCurrentSessionXForms() {
		//	All intervals are in seconds
		long liveTime = 999999999;												//	Maximal time
		long ttlIdle = wipingInterval == 0 ? liveTime : wipingInterval / 1000;	//	Can be defined in configuration XML file
		long ttlCache = timeOut == 0 ? liveTime : timeOut / 1000;				//	Can be defined in configuration XML file
		try {
			//	TODO: the elements of a cache are not serializable, can not flush!
			return IWCacheManager2.getInstance(IWMainApplication.getDefaultIWMainApplication()).getCache(XFORMS_SESSIONS_CACHE_NAME, getMaxSessions(),
					Boolean.FALSE, Boolean.FALSE, ttlIdle, ttlCache, Boolean.FALSE, getCacheListener(), getCacheGuardian());
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting cache of XForms (" + XFORMS_SESSIONS_CACHE_NAME + ")", e);
			CoreUtil.sendExceptionNotification(e);
		}
		
		return null;
	}
	
	public static final int getMaxSessions() {
		int sessions = Integer.valueOf(IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty("max_xforms_sessions", String.valueOf(1000)));
		
		if (maxSessions != sessions)
			maxSessions = sessions;
		
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
				if (ListUtil.isEmpty(ids)) {
					return;
				}
				
				for (String id: ids) {
					XFormsSession session = getXFormsSession(id);
					if (session instanceof IdegaXFormsSessionBase) {
						IdegaXFormsSessionBase xformSession = (IdegaXFormsSessionBase) session;
						String httpSession = xformSession.getHttpSessionId();
						if (isXFormSessionReadyToBeDeleted(xformSession)) {
							//	Removing XForms session because original HTTP session has expired
							invalidateXFormsSession(session, id, "HTTP session " + httpSession +
									" already is invalid. It was invalidated by web application earlier.");
						} else {
							//	Keeping session alive!
							session.updateLRU();
							if (IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("print_xform_keep_alive", Boolean.TRUE)) {
								LOGGER.info("Keeping XForm session " + id + " alive for HTTP session: " + httpSession);
							}
						}
					}
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error while trying to keep alive XForms sessions: " + ids);
			} finally {
				getSessionCount();
			}
		} else if (event instanceof HttpSessionDestroyed) {
			HttpSessionDestroyed destroyed = (HttpSessionDestroyed) event;
			invalidateXFormsSessions(destroyed.getHttpSessionId(), new Date(destroyed.getLastTimeAccessed()), destroyed.getMaxInactiveInterval());
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
		
		List<Element> divs = XmlUtil.getElementsByXPath(document, "div", XmlUtil.XHTML_NAMESPACE_ID);
		if (ListUtil.isEmpty(divs)) {
			return;
		}
		
		boolean sessionDestroyed = false;
		for (Iterator<Element> divsIter = divs.iterator(); (divsIter.hasNext() && !sessionDestroyed);) {
			Element div = divsIter.next();
			
			Attribute id = div.getAttribute("id");
			if (id == null) {
				continue;
			}
			
			String idValue = id.getValue();
			if (StringUtil.isEmpty(idValue) || !idValue.equals("chibaXFormSessionKeyContainerId")) {
				continue;
			}
			
			Attribute title = div.getAttribute("title");
			if (title == null) {
				continue;
			}
			
			String sessionId = title.getValue();
			XFormsSession session = getXFormsSession(sessionId);
			if (session == null) {
				continue;
			}
			
			invalidateXFormsSession(session, sessionId, "PDF document was just generated using this XForm session - it is not needed anymore");
			sessionDestroyed = true;
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
	
	private boolean isXFormSessionReadyToBeDeleted(IdegaXFormsSessionBase xformSession) {
		if (xformSession == null) {
			return true;
		}
		
		Class<? extends HttpSession> httpSessionClass = xformSession.getSessionClass();
		if (httpSessionClass != null && httpSessionClass.equals(IdegaXFormHttpSession.class)) {
			IWTimestamp lastTimeUsed = new IWTimestamp(xformSession.getLastUseTime());
			IWTimestamp weekAgo = IWTimestamp.RightNow();
			weekAgo.setDay(weekAgo.getDay() - 7);
			return lastTimeUsed.isEarlierThan(weekAgo);	//	Not keeping XForm session if it was not used during last week
		}
		
		return !getHttpSessionsManager().isSessionValid(xformSession.getHttpSessionId());	//	Simply checking if HttpSession is valid
	}
	
	private void invalidateXFormsSessions(String httpSessionId, Date lastTimeAccessed, int maxInactiveInterval) {
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
					IdegaXFormsSessionBase xformSession = (IdegaXFormsSessionBase) session;
					if (httpSessionId.equals(xformSession.getHttpSessionId()) && isXFormSessionReadyToBeDeleted(xformSession)) {
						invalidateXFormsSession(session, xFormSessionId, "HTTP session " + httpSessionId +
								" was just invalidated by web application. The last time it was accessed: " + lastTimeAccessed +
								". It was inactive for: " + (System.currentTimeMillis() - lastTimeAccessed.getTime()) +
								" milliseconds. It is allowed to be inactive for " + maxInactiveInterval + " seconds.");
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error while trying invalidate XForms sessions tied to destroyed HttpSession: " + httpSessionId, e);
		}
	}
	
	public void invalidateXFormsSession(XFormsSession session, String id, String explanation) {
		String httpSession = null;
		if (session instanceof IdegaXFormsSessionBase) {
			httpSession = ((IdegaXFormsSessionBase) session).getHttpSessionId();
		}
		if (StringUtil.isEmpty(httpSession)) {
			httpSession = "unknown";
		}
		
		try {
			WebAdapter adapter = session.getAdapter();
	        if (adapter != null) {
	        	adapter.shutdown();
	        }
		} catch (Exception e) {
		} finally {
			if (explanation == null) {
				explanation = CoreConstants.EMPTY;
			}
			explanation = "Deleted XForm session manually. ".concat(explanation);
			
			ChibaUtils.getInstance().markXFormSessionFinished(session, Boolean.TRUE);
			deleteXFormsSession(id, explanation);
		}
	}

	public IWHttpSessionsManager getHttpSessionsManager() {
		return httpSessionsManager;
	}

	public void setHttpSessionsManager(IWHttpSessionsManager httpSessionsManager) {
		this.httpSessionsManager = httpSessionsManager;
	}
	
	public Set<String> getKeysOfActiveSessions() {
		Map<String, XFormsSession> sessions = getCurrentSessionXForms();
		if (sessions == null) {
			return null;
		}
		return sessions.keySet();
	}
}