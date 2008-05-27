package com.idega.chiba.facade;

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
public class FluxExceptionHandler extends FluxFacade {
	private String errorMsg = "Flux error";

	public FluxExceptionHandler() {
		super();
	}

	public Element fireAction(String id, String sessionKey)
			throws FluxException {
		Element element = null;
		try {
			element = super.fireAction(id, sessionKey);
		} catch (Exception e) {
//			throw new FluxException(errorMsg);
    	} finally {
			return element;
		}
	}

	public Element setXFormsValue(String id, String value, String sessionKey)
			throws FluxException {
		Element element = null;
		try {
			element = super.setXFormsValue(id, value, sessionKey);
		} catch (Exception e) {
//			throw new FluxException(errorMsg);
		} finally {
			return element;
		}
	}

	public Element setRepeatIndex(String id, String position, String sessionKey)
			throws FluxException {
		Element element = null;
		try {
			element = super.setRepeatIndex(id, position, sessionKey);
		} catch (Exception e) {
//			throw new FluxException(errorMsg);
		} finally {
			return element;
		}
	}
	
	public Element fetchProgress(String id, String filename, String sessionKey) {
		Element element = null;
		try {
			element = super.fetchProgress(id, filename, sessionKey);
		} catch (Exception e) {
//			throw new FluxException(errorMsg);
		} finally {
			return element;
		}
	}
	
	public void keepAlive(String sessionKey) {
		try {
			super.keepAlive(sessionKey);
		} catch (Exception e) {
			//TODO implement exception handling
		}
	}
	 
    public void close(String sessionKey){
		try {
			super.close(sessionKey);
		} catch (Exception e) {
			//TODO implement exception handling
		}
    }
}
