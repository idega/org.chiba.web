package com.idega.chiba;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.chiba.web.IWBundleStarter;
import org.chiba.web.session.XFormsSession;
import org.chiba.web.session.XFormsSessionManager;
import org.chiba.web.upload.UploadInfo;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.chiba.web.exception.IdegaChibaException;
import com.idega.chiba.web.session.impl.IdegaXFormSessionManagerImpl;
import com.idega.chiba.web.session.impl.IdegaXFormsSessionBase;
import com.idega.core.business.DefaultSpringBean;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ChibaUtils extends DefaultSpringBean {
	
	private static final Logger LOGGER = Logger.getLogger(ChibaUtils.class.getName());
	
	private static ChibaUtils instance;
	
	private static final String UPLOAD_INFO_STATUS_FAILED = "failed";
	
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
    	XFormsSession session = getXFormSession(sessionKey);
    	return session == null ? CoreConstants.EMPTY : ". Object found for this key: " + session;
    }
    
    public Set<String> getKeysOfCurrentSessions() {
    	IWContext iwc = CoreUtil.getIWContext();
    	if (iwc == null || !iwc.isLoggedOn()) {
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
    
    private XFormsSession getXFormSession(String key) {
    	return IdegaXFormSessionManagerImpl.getXFormsSessionManager().getXFormsSession(key);
    }
    
    public int getNumberOfXFormSessionsForHttpSession(String httpSessionId) {
    	if (StringUtil.isEmpty(httpSessionId)) {
    		return 0;
    	}
    	
    	Set<String> xformSessions = getKeysOfCurrentSessions();
    	if (ListUtil.isEmpty(xformSessions)) {
    		return 0;
    	}
    	
    	int number = 0;
    	for (String xformSessionId: xformSessions) {
    		XFormsSession session = getXFormSession(xformSessionId);
    		if (session instanceof IdegaXFormsSessionBase) {
    			if (httpSessionId.equals(((IdegaXFormsSessionBase) session).getHttpSessionId())) {
    				number++;
    			}
    		}
    	}
    	
    	return number;
    }
    
    private String getUploadInfoKey(String sessionKey) {
    	return XFormsSession.ADAPTER_PREFIX.concat(sessionKey).concat("-uploadInfo");
    }
    
    private UploadInfo getUploadInfo(HttpSession session, String sessionKey) {
    	if (session == null) {
    		return null;
    	}
    	
    	Object info = session.getAttribute(getUploadInfoKey(sessionKey));
    	return info instanceof UploadInfo ? (UploadInfo) info : null;
    }
    
    public void markUploadAsFailed(String sessionKey) {
    	markUploadAsFailed(getSession(), sessionKey);
    }
    
    public void markUploadAsFailed(HttpSession session, String sessionKey) {
    	UploadInfo info = getUploadInfo(session, sessionKey);
    	if (info == null) {
    		return;
    	}
		
    	info.setStatus(UPLOAD_INFO_STATUS_FAILED);
	}
    
    public boolean isUploadInvalid(HttpSession session, String sessionKey) {
    	UploadInfo info = getUploadInfo(session, sessionKey);
    	if (info == null) {
    		return Boolean.FALSE;
    	}
    	
    	if (UPLOAD_INFO_STATUS_FAILED.equals(info.getStatus())) {
    		session.removeAttribute(getUploadInfoKey(sessionKey));
    		return Boolean.TRUE;
    	}
    	
    	return Boolean.FALSE;
    }
    
    public String getCurrentHttpSessionId() {
    	HttpSession session = getSession();
    	return session == null ? CoreConstants.MINUS : session.getId();
    }
}