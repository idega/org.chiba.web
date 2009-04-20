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

/**
 * Idega implementation of a XFormsSessionManager.
 *
 * @author Anton Makarov</a>
 */

public class IdegaXFormSessionManagerImpl implements XFormsSessionManager {	
    	
	private static final String XFORM_SESSIONS_ATTR_NAME = "com.idega.chiba.web.session.impl.IdegaXFormSessionManagerImpl.XFORM_SESSIONS_ATTR_NAME";

	protected static XFormsSessionManager instance = null;
	
    protected Map<String, XFormsSession> xformsSessions;

	
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
        //	e.printStackTrace();
        	Logger.getLogger(IdegaXFormSessionManagerImpl.class.getName()).log(Level.WARNING, "Exception while trying to create xforms session", e);
        }
    	
        XFormsSession xFormsSessionBase = new IdegaXFormsSessionBase(request,response,session);
     /*   if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("created XFormsSession: " + xFormsSessionBase.getKey());
        }*/
        return xFormsSessionBase;
    }

    /**
     * Register XFormSession.
     */
    public void addXFormsSession(XFormsSession xfSession) {
       
    	getCurrentSessionXForms().put(xfSession.getKey(), xfSession);
       
    /*    if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added XFormsSession to SessionManager: " + xfSession.getKey());
        }*/
    }

    
    /**
     * Remove XFormsSession from HttpSession.
     *
     * @param id the XFormsSession id
     */
    public void deleteXFormsSession(String id) {

    	//xformsSessions = getCurrentSessionXForms();
    	
    	getCurrentSessionXForms().remove(id);
        /*if (removed != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("deleted XFormsSession from SessionManager: " + id);
            }
        }*/
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

    	XFormsSession formsSession = getXformsSessions().get(id);
        if (formsSession != null) {
            /*if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("returning XFormsSession: " + id);
            }*/
            return formsSession;
        } else {
        /*    if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("XFormsSession: " + id + " not found");
            }*/
            return null;
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
    	
		HttpSession session = IWContext.getCurrentInstance().getSession();
		setXformsSessions((Map<String, XFormsSession>) session.getAttribute(XFORM_SESSIONS_ATTR_NAME));
		
		if (getXformsSessions() == null) {
			
			setXformsSessions(new HashMap<String, XFormsSession>());
			session.setAttribute(XFORM_SESSIONS_ATTR_NAME, getXformsSessions());
			
		}
		return getXformsSessions();
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
	
	 protected Map<String, XFormsSession> getXformsSessions() {
	    	return xformsSessions;
	 }

	 protected void setXformsSessions(Map<String, XFormsSession> xformsSessions) {
	    	this.xformsSessions = xformsSessions;
	 }
}
