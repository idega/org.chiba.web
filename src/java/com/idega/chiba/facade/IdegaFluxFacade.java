package com.idega.chiba.facade;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.chiba.web.flux.FluxException;
import org.chiba.web.flux.FluxFacade;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

/**
 * 
 * 
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version Revision: 1.0
 * 
 * Last modified: May 27, 2008 by Author: Anton
 * 
 */
@Scope("request")
@Service("fluxexhand")
public class IdegaFluxFacade extends FluxFacade {
	
	public Element fireAction(String id, String sessionKey) {
		
		try {
			return super.fireAction(id, sessionKey);
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception while firing action, session key="+sessionKey, e);
			return null;
    	}
	}

	public Element setXFormsValue(String id, String value, String sessionKey) throws FluxException {
		
		try {
			return super.setXFormsValue(id, value, sessionKey);
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception while setting xforms value, session key="+sessionKey, e);
			return null;
		}
	}

	public Element setRepeatIndex(String id, String position, String sessionKey) throws FluxException {
		try {
			return super.setRepeatIndex(id, position, sessionKey);
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception while setting repeat index, session key="+sessionKey, e);
			return null;
		}
	}
	
	public Element fetchProgress(String id, String filename, String sessionKey) {
		try {
			return super.fetchProgress(id, filename, sessionKey);
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception while fetching progress", e);
			return null;
		}
	}
	
	public void keepAlive(String sessionKey) {
		try {
			super.keepAlive(sessionKey);
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception at keep alive, session key="+sessionKey, e);
		}
	}
	 
    public void close(String sessionKey){
		try {
			super.close(sessionKey);
		} catch (Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception at close, session key="+sessionKey, e);
		}
    }
}