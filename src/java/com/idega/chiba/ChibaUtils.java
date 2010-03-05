package com.idega.chiba;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.chiba.web.IWBundleStarter;
import org.chiba.web.session.XFormsSession;
import org.chiba.web.session.XFormsSessionManager;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.chiba.web.exception.IdegaChibaException;
import com.idega.chiba.web.session.impl.IdegaXFormSessionManagerImpl;
import com.idega.core.business.DefaultSpringBean;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ChibaUtils extends DefaultSpringBean {
	
	private static final Logger LOGGER = Logger.getLogger(ChibaUtils.class.getName());
	
	private static ChibaUtils instance;
	
	private ChibaUtils() {
		instance = this;
	}

	public static final ChibaUtils getInstance() {
		return instance;
	}
	
	public String getSessionExpiredLocalizedString() {
		return getLocalizedString("chiba.session_expired_messsage", "Your session has expired. Please try again.");
	}
	
	public String getInternalErrorLocalizedString() {
		return getLocalizedString("chiba.internal_error", "Sorry, some internal error has occurred... Page should be reloaded...");
	}
	
	public String getLocalizedString(String key, String defaultValue) {
		try {
			IWBundle bundle = IWMainApplication.getDefaultIWMainApplication().getBundle(IWBundleStarter.BUNDLE_IDENTIFIER);
			IWResourceBundle iwrb = getResourceBundle(bundle);
			return iwrb == null ? defaultValue : iwrb.getLocalizedString(key, defaultValue);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting localization for: " + key, e);
		}
		return defaultValue;
	}
	
	public String getSessionKey(HttpServletRequest request) {
		return request == null ? null : request.getParameter("sessionKey");
	}
	
	public void prepareForChibaMethod(HttpServletRequest request) throws IdegaChibaException {
		if (request == null) {
			throw new IdegaChibaException("HttpServletRequest is undefined!", getInternalErrorLocalizedString(), Boolean.TRUE);
		}
		
		String chibaSessionKey = getSessionKey(request);
		prepareForChibaMethod(request.getSession(Boolean.TRUE), chibaSessionKey);
	}
	
	public void prepareForChibaMethod(HttpSession httpSession, String chibaSessionKey) throws IdegaChibaException {
		if (StringUtil.isEmpty(chibaSessionKey)) {
			throw new IdegaChibaException("Session key is undefined!", getInternalErrorLocalizedString(), Boolean.TRUE);
		}
		
		XFormsSession xformSession = IdegaXFormSessionManagerImpl.getXFormsSessionManager().getXFormsSession(chibaSessionKey);
		if (xformSession == null) {
			throw new IdegaChibaException("XForm session was not found by key: " + chibaSessionKey, getSessionExpiredLocalizedString(), Boolean.TRUE);
		}
		
		if (httpSession == null) {
			LOGGER.warning("HTTP session object is undefined for XForm session: " + chibaSessionKey);
			return;
		}
		if (!(httpSession.getAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER) instanceof IdegaXFormSessionManagerImpl)) {
			httpSession.setAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER, IdegaXFormSessionManagerImpl.getXFormsSessionManager());
		}
	}
	
	public IdegaChibaException getIdegaChibaException(String sessionKey, String exceptionMessage, String localizationKey, String defaultLocalizationValue) {
    	return getIdegaChibaException(sessionKey, exceptionMessage, ChibaUtils.getInstance().getLocalizedString(localizationKey, defaultLocalizationValue));
    }
    
    public IdegaChibaException getIdegaChibaException(String sessionKey, String exceptionMessage, String localizedMessage) {
    	boolean reloadPage = true;
		String messageToTheClient = null;
		if (CoreConstants.EMPTY.equals(getSessionInformation(sessionKey))) {
			messageToTheClient = getSessionExpiredLocalizedString();
		} else {
			reloadPage = false;
			messageToTheClient = localizedMessage;
		}
		
		return new IdegaChibaException(exceptionMessage, messageToTheClient, reloadPage);
    }
    
    public String getSessionInformation(String sessionKey) {
    	XFormsSession session = IdegaXFormSessionManagerImpl.getXFormsSessionManager().getXFormsSession(sessionKey);
    	return session == null ? CoreConstants.EMPTY : ". Object found for this key: " + session;
    }
}