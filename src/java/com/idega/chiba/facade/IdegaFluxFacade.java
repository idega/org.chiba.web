package com.idega.chiba.facade;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.chiba.web.flux.FluxException;
import org.chiba.web.flux.FluxFacade;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import com.idega.chiba.ChibaUtils;
import com.idega.chiba.web.exception.SessionExpiredException;
import com.idega.chiba.web.session.impl.IdegaXFormSessionManagerImpl;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.servlet.filter.RequestResponseProvider;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.SendMail;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

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
	
	private HttpSession session;
	
	public IdegaFluxFacade() {
		super();
		
		try {
			RequestResponseProvider provider = ELUtil.getInstance().getBean(RequestResponseProvider.class);
			this.session = provider.getRequest().getSession(Boolean.TRUE);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error while trying to get HTTP session object", e);
		}
	}
	
	@Override
	public Element fireAction(String id, String sessionKey) throws FluxException {
		try {
			ChibaUtils.getInstance().prepareForChibaMethod(session, sessionKey);
			
			return super.fireAction(id, sessionKey);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error firing action", e);
			throw new SessionExpiredException("Unable to fire action for element: '".concat(id).concat("' using session: ").concat(sessionKey)
					.concat(ChibaUtils.getInstance().getSessionInformation(sessionKey)), e, ChibaUtils.getInstance().getSessionExpiredLocalizedString());
		}
	}

	@Override
	public Element setXFormsValue(String id, String value, String sessionKey) throws FluxException {
		try {
			ChibaUtils.getInstance().prepareForChibaMethod(session, sessionKey);
			
			return super.setXFormsValue(id, value, sessionKey);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error setting value to XForm", e);
			throw new SessionExpiredException("Unable to set value '".concat(value).concat("' for element '").concat(id).concat("' using session: ")
					.concat(sessionKey).concat(ChibaUtils.getInstance().getSessionInformation(sessionKey)), e,
					ChibaUtils.getInstance().getSessionExpiredLocalizedString());
		}
	}

	@Override
	public Element setRepeatIndex(String id, String position, String sessionKey) throws FluxException {
		try {
			ChibaUtils.getInstance().prepareForChibaMethod(session, sessionKey);
			
			return super.setRepeatIndex(id, position, sessionKey);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error setting repeat index", e);
			throw new SessionExpiredException("Unable to set repeat index for element: '".concat(id).concat("', position: '").concat(position)
					.concat("' using session: ").concat(sessionKey).concat(ChibaUtils.getInstance().getSessionInformation(sessionKey)), e,
					ChibaUtils.getInstance().getSessionExpiredLocalizedString());
		}	
	}
	
	@Override
	public Element fetchProgress(String id, String filename, String sessionKey) {
		try {
			ChibaUtils.getInstance().prepareForChibaMethod(session, sessionKey);
			
			if (ChibaUtils.getInstance().isUploadInvalid(session, sessionKey)) {
				throw ChibaUtils.getInstance().getIdegaChibaException(sessionKey, "Upload is marked as failed", "chiba.uploading_failed",
					"Sorry, uploading failed. Please try again.");
			}
			
			return super.fetchProgress(id, filename, sessionKey);
		} catch (Exception e) {
			String message = "Exception while fetching progress for element: '".concat(id).concat("', file: '").concat(filename).concat("' using session: ")
				.concat(sessionKey).concat(ChibaUtils.getInstance().getSessionInformation(sessionKey));
			LOGGER.log(Level.SEVERE, message, e);
			CoreUtil.sendExceptionNotification(message, e);
			
			throw ChibaUtils.getInstance().getIdegaChibaException(sessionKey, e.getMessage(), "chiba.uploading_failed",
					"Sorry, uploading failed. Please try again.");
		}
	}
	
	@Override
	public void keepAlive(String sessionKey) {
		try {
			ChibaUtils.getInstance().prepareForChibaMethod(session, sessionKey);
			
			super.keepAlive(sessionKey);
		} catch (Exception e) {
			String message = "Exception at keep alive, session key=".concat(sessionKey).concat(ChibaUtils.getInstance().getSessionInformation(sessionKey));
			LOGGER.log(Level.SEVERE, message, e);
			CoreUtil.sendExceptionNotification(message, e);
			
			throw ChibaUtils.getInstance().getIdegaChibaException(sessionKey, e.getMessage(), ChibaUtils.getInstance().getInternalErrorLocalizedString());
		}			
	}
	 
    @Override
	public void close(String sessionKey) {	
    	try {
    		ChibaUtils.getInstance().markXFormSessionFinished(sessionKey, Boolean.TRUE);
    		
			super.close(sessionKey);
		} catch (Exception e) {
			String message = "Exception at close, session key=".concat(sessionKey).concat(ChibaUtils.getInstance().getSessionInformation(sessionKey));
			LOGGER.log(Level.SEVERE, message, e);
			CoreUtil.sendExceptionNotification(message, e);
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
    
    public int getNumberOfActiveSessions() {
    	return IdegaXFormSessionManagerImpl.getXFormsSessionManager().getSessionCount();
    }
    
    public Set<String> getKeysOfCurrentSessions() {
    	return ChibaUtils.getInstance().getKeysOfCurrentSessions();
    }
    
    public List<String> getInfoAboutCurrentSessions() {
    	return ChibaUtils.getInstance().getInfoAboutCurrentSessions();
    }
    
    public boolean deleteXFormSessionManually(String key) {
    	return ChibaUtils.getInstance().deleteXFormSessionManually(key);
    }
}