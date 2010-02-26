package com.idega.chiba.facade;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.chiba.web.IWBundleStarter;
import org.chiba.web.flux.FluxException;
import org.chiba.web.flux.FluxFacade;
import org.chiba.web.session.XFormsSession;
import org.chiba.web.session.XFormsSessionManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import com.idega.chiba.web.exception.IdegaChibaException;
import com.idega.chiba.web.exception.SessionExpiredException;
import com.idega.chiba.web.session.impl.IdegaXFormSessionManagerImpl;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.SendMail;
import com.idega.util.StringUtil;

/**
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
			throw new SessionExpiredException("Unable to fire action for element: '".concat(id).concat("' using session: ").concat(sessionKey)
					.concat(getSessionInformation(sessionKey)), e, getSessionExpiredLocalizedString());
		}
	}

	@Override
	public Element setXFormsValue(String id, String value, String sessionKey) throws FluxException {
		try {
			return super.setXFormsValue(id, value, sessionKey);
		} catch (Exception e) {
			throw new SessionExpiredException("Unable to set value '".concat(value).concat("' for element '").concat(id).concat("' using session: ")
					.concat(sessionKey).concat(getSessionInformation(sessionKey)), e, getSessionExpiredLocalizedString());
		}
	}

	@Override
	public Element setRepeatIndex(String id, String position, String sessionKey) throws FluxException {
		try {
			return super.setRepeatIndex(id, position, sessionKey);
		} catch (Exception e) {
			throw new SessionExpiredException("Unable to set repeat index for element: '".concat(id).concat("', position: '").concat(position)
					.concat("' using session: ").concat(sessionKey).concat(getSessionInformation(sessionKey)), e, getSessionExpiredLocalizedString());
		}	
	}
	
	@Override
	public Element fetchProgress(String id, String filename, String sessionKey) {
		try {
			return super.fetchProgress(id, filename, sessionKey);
		} catch (Exception e) {
			String message = "Exception while fetching progress for element: '".concat(id).concat("', file: '").concat(filename).concat("' using session: ")
				.concat(sessionKey).concat(getSessionInformation(sessionKey));
			LOGGER.log(Level.SEVERE, message, e);
			CoreUtil.sendExceptionNotification(message, e);
			
			throw getIdegaChibaException(sessionKey, e.getMessage(), "chiba.uploading_failed", "Sorry, uploading failed. Please try again.");
		}
	}
	
	@Override
	public void keepAlive(String sessionKey) {
		try {
			super.keepAlive(sessionKey);
		} catch (Exception e) {
			String message = "Exception at keep alive, session key=".concat(sessionKey).concat(getSessionInformation(sessionKey));
			LOGGER.log(Level.SEVERE, message, e);
			CoreUtil.sendExceptionNotification(message, e);
			
			throw getIdegaChibaException(sessionKey, e.getMessage(), "chiba.error_keeping_session_alive", "Sorry, some internal error occurred...");
		}			
	}
	 
    @Override
	public void close(String sessionKey) {	
    	try {
			super.close(sessionKey);
		} catch (Exception e) {
			String message = "Exception at close, session key=".concat(sessionKey).concat(getSessionInformation(sessionKey));
			LOGGER.log(Level.SEVERE, message, e);
			CoreUtil.sendExceptionNotification(message, e);
		}	
    }
    
    private IdegaChibaException getIdegaChibaException(String sessionKey, String exceptionMessage, String localizationKey, String defaultLocalizationValue) {
    	boolean reloadPage = true;
		String messageToTheClient = null;
		if (CoreConstants.EMPTY.equals(getSessionInformation(sessionKey))) {
			messageToTheClient = getSessionExpiredLocalizedString();
		} else {
			reloadPage = false;
			messageToTheClient = getLocalizedString(localizationKey, defaultLocalizationValue);
		}
		
		return new IdegaChibaException(exceptionMessage, messageToTheClient, reloadPage);
    }
    
    private String getSessionInformation(String sessionKey) {
    	XFormsSession session = IdegaXFormSessionManagerImpl.getXFormsSessionManager().getXFormsSession(sessionKey);
    	return session == null ? CoreConstants.EMPTY : ". Object found for this key: " + session;
    }
    
    private String getSessionExpiredLocalizedString() {
    	return getLocalizedString("chiba.session_expired_messsage", "Your session has expired. Please try again.");
    }
    
    private String getLocalizedString(String key, String defaultValue) {
    	try {
	    	IWBundle bundle = IWMainApplication.getDefaultIWMainApplication().getBundle(IWBundleStarter.BUNDLE_IDENTIFIER);
	    	IWResourceBundle iwrb = bundle.getResourceBundle(CoreUtil.getIWContext());
	    	return iwrb == null ? defaultValue : iwrb.getLocalizedString(key, defaultValue);
    	} catch (Exception e) {
    		LOGGER.log(Level.WARNING, "Error getting localization for: " + key, e);
    	}
    	return defaultValue;
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
    
    public int getNumberOfActiveSessions() {
    	return IdegaXFormSessionManagerImpl.getXFormsSessionManager().getSessionCount();
    }
    
    public Set<String> getKeysOfCurrentSessions() {
    	IWContext iwc = CoreUtil.getIWContext();
    	if (!iwc.isLoggedOn()) {
    		LOGGER.warning("User must be logged!");
    		return null;
    	}
    	if (!iwc.isSuperAdmin()) {
    		LOGGER.warning("User does not have enough rights!");
    		return null;
    	}
    	
    	XFormsSessionManager manager = IdegaXFormSessionManagerImpl.getXFormsSessionManager();
    	return manager instanceof IdegaXFormSessionManagerImpl ? ((IdegaXFormSessionManagerImpl) manager).getKeysOfActiveSessions() : null;
    }
}