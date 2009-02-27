package com.idega.chiba.web.session.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chiba.web.IWBundleStarter;
import org.chiba.web.WebFactory;
import org.chiba.web.session.XFormsSession;
import org.chiba.web.session.XFormsSessionManager;
import org.chiba.xml.xforms.config.Config;
import org.chiba.xml.xforms.exception.XFormsException;

/**
 * Idega implementation of a XFormsSessionManager.
 *
 * @author Anton Makarov</a>
 */

public class IdegaXFormSessionManagerImpl implements XFormsSessionManager, Runnable {	
    private static final Log LOGGER = LogFactory.getLog(IdegaXFormSessionManagerImpl.class);
	
	protected static XFormsSessionManager instance = null;
    protected Map<String, XFormsSession> xformsSessions;
    protected int maxSessions;
    
    private static final int DEFAULT_TIMEOUT = 60 * 60 * 1000; //default is 60 Minutes (expressed in milliseconds)
    private int timeout = DEFAULT_TIMEOUT;
    private boolean stopped = false;
    private long interval = 1500;
    private boolean threadStarted = false;
    private Object monitor = new Object();
	
    public static XFormsSessionManager getXFormsSessionManager() {
    	if (instance == null) {
            instance = new IdegaXFormSessionManagerImpl();
        }
        return instance;
    }
    
    private IdegaXFormSessionManagerImpl() {    	
        this.xformsSessions = Collections.synchronizedMap(new HashMap<String, XFormsSession>());
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
        	e.printStackTrace();
        }
    	
        XFormsSession xFormsSessionBase = new IdegaXFormsSessionBase(request,response,session);
        //this.xformsSessions.put(xFormsSession.getKey(), xFormsSession);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("created XFormsSession: " + xFormsSessionBase.getKey());
        }
        return xFormsSessionBase;
    }

    /**
     * must be called to register a XFormsSession with the Manager
     */
    public void addXFormsSession(XFormsSession xfSession) {
        this.xformsSessions.put(xfSession.getKey(), xfSession);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added XFormsSession to SessionManager: " + xfSession.getKey());
            LOGGER.debug("Session count now: " + xformsSessions.size());
        }
    }

    /**
     * deletes  XFormsSession object from internal pool of objects.
     *
     * @param id the XFormsSession id
     */
    public void deleteXFormsSession(String id) {

        if (this.xformsSessions.containsKey(id)) {
            this.xformsSessions.remove(id);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("deleted XFormsSession from SessionManager: " + id);
                LOGGER.debug("Session count now: " + xformsSessions.size());
            }
        }
    }

    public void destroy() {
    	synchronized (monitor) {
            LOGGER.info("cleanups allocated resources");
            this.stopped = true;
            instance = null;
            monitor.notifyAll();

        }
    }

    public int getSessionCount() {
        return this.xformsSessions.size();
    }

    /**
     * fetches a XFormsSession by its id
     *
     * @param id the id of the session as created during createXFormsSession() call
     * @return returns the XFormsSession object associated with given id or null if object does not exist
     */
    public XFormsSession getXFormsSession(String id) {

        if (this.xformsSessions.containsKey(id)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("returning XFormsSession: " + id);
                LOGGER.debug("Session count now: " + xformsSessions.size());
            }
            return this.xformsSessions.get(id);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("XFormsSession: " + id + " not found");
            }
            return null;
        }
    }

    public synchronized void init() {
        if (this.interval != 0 && !threadStarted) {
            new Thread(this).start();
            this.threadStarted = true;
        } else {
            LOGGER.warn("No XForms session cleanup. Your server might run out of memory under load. To avoid this configure your chiba-config accordingly.");
        }

    }

    /**
     * set the interval the wiper thread will check for expired sessions. Setting a value of 0 causes the wiper
     * thread to be *not* started. Therefore XFormsSessions will never expire unless the Http Session is still
     * alive.
     *
     * @param milliseconds the interval the wiper is checking for expired sessions
     */
    public void setInterval(int milliseconds) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("checking interval in seconds: " + milliseconds / 1000);
        }
        this.interval = milliseconds;
    }

    /**
     * set the maximum amount of transient (in-memory) sessions allowed for a user
     *
     * @param max the maximum amount of transient (in-memory) sessions allowed for a user
     */
    public void setMaxSessions(int max) {
        this.maxSessions = max;
    }

    public void setTimeout(int milliseconds) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("timeout in seconds: " + milliseconds / 1000);
        }
        this.timeout = milliseconds;
    }
    
    public void run() {
    	synchronized (monitor) {
            while (!stopped) {
                wipe();
                try {
                	monitor.wait(interval);
                } catch (InterruptedException e) {
                    LOGGER.error("Exception while trying to sleep Thread");
                }
            }
        }
    }
    
    /**
     * checks for timed-out XFormsSessions and deletes these from internal pool.
     */
    private void wipe() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checking for expired sessions at " + new Date(System.currentTimeMillis()));
        }

        XFormsSession session;
        Iterator<XFormsSession> allSessions = this.xformsSessions.values().iterator();
        while (allSessions.hasNext()) {
            session = allSessions.next();

            if (isExpired(session)) {
                allSessions.remove();
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Session count now: " + xformsSessions.size());
        }
    }
    
    private boolean isExpired(XFormsSession session) {
        long now = System.currentTimeMillis();

        if ((now - session.getLastUseTime()) > (timeout)) {
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("expiring session " + session.getKey() + " lastused: " + new Date(session.getLastUseTime()));
            }

            return true;
        } else {
            return false;
        }
    }
}
