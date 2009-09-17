package com.idega.chiba.facade;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.chiba.web.flux.FluxException;
import org.chiba.web.flux.FluxFacade;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import com.idega.chiba.web.exception.SessionExpiredException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.SendMail;
import com.idega.util.StringUtil;

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
	
	private static final Logger LOGGER = Logger.getLogger(IdegaFluxFacade.class.getName());
	
	@Override
	public Element fireAction(String id, String sessionKey) throws FluxException {
		try {
			return super.fireAction(id, sessionKey);
		} catch (Exception e) {
			throw new SessionExpiredException("Unable to fire action for element: '" + id + "' using session: " + sessionKey, e);
		}
	}

	@Override
	public Element setXFormsValue(String id, String value, String sessionKey) throws FluxException {
		try {
			return super.setXFormsValue(id, value, sessionKey);
		} catch (FluxException e) {
			throw new SessionExpiredException("Unable to set value '" + value + "' for element '" + id + "' using session: " + sessionKey, e);
		}
	}

	@Override
	public Element setRepeatIndex(String id, String position, String sessionKey) throws FluxException {
		try {
			return super.setRepeatIndex(id, position, sessionKey);
		} catch (Exception e) {
			throw new SessionExpiredException("Unable to set repeat index for element: '"+ id +"', position: '"+ position +"' using session: " + sessionKey, e);
		}	
	}
	
	@Override
	public Element fetchProgress(String id, String filename, String sessionKey) {
		try {
			return super.fetchProgress(id, filename, sessionKey);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception while fetching progress for element: '" + id + "', file: '" + filename + "' using session: " + sessionKey, e);
			CoreUtil.sendExceptionNotification(e);
			return null;
		}
	}
	
	@Override
	public void keepAlive(String sessionKey) {	
		try {
			super.keepAlive(sessionKey);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception at keep alive, session key=" + sessionKey, e);
			CoreUtil.sendExceptionNotification(e);
		}			
	}
	 
    @Override
	public void close(String sessionKey) {	
    	try {
			super.close(sessionKey);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception at close, session key=" + sessionKey, e);
			CoreUtil.sendExceptionNotification(e);
		}	
    }
    
    public boolean sendEmail(String subject, String text) {
    	if (StringUtil.isEmpty(subject) || StringUtil.isEmpty(text)) {
    		LOGGER.warning("Subject or/and message not provided");
    		return false;
    	}
    	
    	String to = IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty("xform_error_mail_to", "programmers@idega.com");
    	if (StringUtil.isEmpty(to)) {
    		return false;
    	}
    	
    	String host = IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty(CoreConstants.PROP_SYSTEM_SMTP_MAILSERVER);
    	if (StringUtil.isEmpty(host)) {
    		return false;
    	}
    	
    	String userName = "Not logged in";
    	IWContext iwc = CoreUtil.getIWContext();
    	if (iwc != null && iwc.isLoggedOn()) {
    		userName = iwc.getCurrentUser().getName();
    	}
    	text += "\nUser: " + userName;
    	
    	try {
    		SendMail.send("idegaweb@idega.com", to, null, null, host, subject, text);
    	} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error while sending email ("+text+") to: " + to, e);
			return false;
		}
    	
    	return true;
    }
    
}