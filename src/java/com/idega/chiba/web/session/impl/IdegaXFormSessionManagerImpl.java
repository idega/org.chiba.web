package com.idega.chiba.web.session.impl;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.chiba.web.IWBundleStarter;
import org.chiba.web.WebFactory;
import org.chiba.web.session.XFormsSession;
import org.chiba.web.session.XFormsSessionManager;
import org.chiba.xml.xforms.config.Config;
import org.chiba.xml.xforms.exception.XFormsException;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.idega.core.cache.IWCacheManager2;
import com.idega.event.SessionPollerEvent;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.CoreUtil;
import com.idega.util.StringUtil;

/**
 * Idega implementation of a XFormsSessionManager.
 *
 * @author Anton Makarov</a>
 */

public class IdegaXFormSessionManagerImpl implements XFormsSessionManager, ApplicationListener {	
    
	private static final Logger LOGGER = Logger.getLogger(IdegaXFormSessionManagerImpl.class.getName());
	
	private static final String XFORMS_SESSIONS_CACHE_NAME = "idegaXFormsSessionsCache";
	
	protected static XFormsSessionManager instance = null;
	
	private int wipingInterval;
	private int maxSessions;
	private int timeOut;
	
    public static XFormsSessionManager getXFormsSessionManager() {
    	if (instance == null) {
            instance = new IdegaXFormSessionManagerImpl();
        }
        return instance;
    }
    
    private IdegaXFormSessionManagerImpl() {
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
        	LOGGER.warning("Unable to remove XForms session from SessionManager: '" + id + "'");
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
		int maxSessions = this.maxSessions == 0 ? 15 : this.maxSessions;
		
		//	All intervals are in seconds
		long halfAnHour = 60 * 60 * 30;	
		long ttlIdle = wipingInterval == 0 ? halfAnHour : wipingInterval / 1000;
		long ttlCache = timeOut == 0 ? halfAnHour : timeOut / 1000;
		
		Map<String, XFormsSession> xforms = null;
		try {
			xforms = IWCacheManager2.getInstance(IWMainApplication.getDefaultIWMainApplication()).getCache(XFORMS_SESSIONS_CACHE_NAME, maxSessions, true, false,
				ttlIdle, ttlCache);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting cache of XForms ("+XFORMS_SESSIONS_CACHE_NAME+")", e);
			CoreUtil.sendExceptionNotification(e);
		}
		return xforms;
	}

	public int getSessionCount() {
		Map<String, XFormsSession> sessions = getCurrentSessionXForms();
		return sessions == null ? -1 : sessions.size();
	}

	public void setInterval(int wipingInterval) {
		this.wipingInterval = wipingInterval;
	}

	public void setMaxSessions(int max) {
		maxSessions = max;
	}

	public void setTimeout(int milliseconds) {
		timeOut = milliseconds;
	}

	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof SessionPollerEvent) {
			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("XForms sessions in cache: " + getSessionCount());
			}
		}
	}
}