package com.idega.chiba.web.session.impl;

import java.util.HashMap;
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

import com.idega.presentation.IWContext;
import com.idega.servlet.filter.RequestProvider;
import com.idega.util.CoreUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

/**
 * Idega implementation of a XFormsSessionManager.
 *
 * @author Anton Makarov</a>
 */

public class IdegaXFormSessionManagerImpl implements XFormsSessionManager {	
    
	private static final Logger LOGGER = Logger.getLogger(IdegaXFormSessionManagerImpl.class.getName());
	
	private static final String XFORM_SESSIONS_ATTR_NAME = "com.idega.chiba.web.session.impl.IdegaXFormSessionManagerImpl.XFORM_SESSIONS_ATTR_NAME";
	
	protected static XFormsSessionManager instance = null;
	
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
        }
    	
        XFormsSession xFormsSessionBase = new IdegaXFormsSessionBase(request,response,session);
        if (LOGGER.isLoggable(Level.INFO)) {
        	LOGGER.info("created XFormsSession: " + xFormsSessionBase.getKey());
        }
        return xFormsSessionBase;
    }

    /**
     * Register XFormSession.
     */
    public void addXFormsSession(XFormsSession xfSession) {
       Map<String, XFormsSession> sessionXForms = getCurrentSessionXForms();
       
       sessionXForms.put(xfSession.getKey(), xfSession);
    }

    
    /**
     * Remove XFormsSession from HttpSession.
     *
     * @param id the XFormsSession id
     */
    public void deleteXFormsSession(String id) {

    	Map<String, XFormsSession> sessionXForms = getCurrentSessionXForms();
    	
    	XFormsSession removed = sessionXForms.remove(id);
        if (removed == null) {
        	LOGGER.warning("unable to remove XFormsSession: " + id);
        } else {
        	LOGGER.info("deleted XFormsSession from SessionManager: " + id);
        }
    }

    public void destroy() {
    	//nothing to clean
    }

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
    	
    	XFormsSession formsSession = forms.get(id);
        if (formsSession == null) {
        	LOGGER.warning("XFormsSession: " + id + " not found");
            return null;
        } else {
        	return formsSession;
        }
    }

    public synchronized void init() {
    	//no need to initialize session cleanup

    }
            
    /**
     * Get xforms in current http session.
     * 
     * @return map of [key,xform].
     */
    @SuppressWarnings("unchecked")
	private Map<String, XFormsSession> getCurrentSessionXForms() {
    	HttpSession session = null;
    	
    	IWContext iwc = CoreUtil.getIWContext();
    	if (iwc == null) {
    		try {
	    		RequestProvider requestProvider = ELUtil.getInstance().getBean(RequestProvider.class);
	    		session = requestProvider.getRequest().getSession();
    		} catch(Exception e) {
    			LOGGER.log(Level.SEVERE, "Error getting request from: " + RequestProvider.class, e);
    		}
    	} else {
    		session = iwc.getSession();
    	}
		
    	if (session == null) {
    		LOGGER.severe("HTTP session is unavailable!");
    	}
    	
		Map<String, XFormsSession> xformsSessions = (Map<String, XFormsSession>) session.getAttribute(XFORM_SESSIONS_ATTR_NAME);
		if (xformsSessions == null) {
			xformsSessions = new HashMap<String, XFormsSession>();
			session.setAttribute(XFORM_SESSIONS_ATTR_NAME, xformsSessions);
		}
		return xformsSessions;
	}

	public int getSessionCount() {
		return 0;
	}

	public void setInterval(int wipingInterval) {
		
	}

	public void setMaxSessions(int max) {
		
	}

	public void setTimeout(int milliseconds) {
		
	}
}
