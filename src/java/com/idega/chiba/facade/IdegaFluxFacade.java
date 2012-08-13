package com.idega.chiba.facade;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.chiba.web.IWBundleStarter;
import org.chiba.web.flux.FluxException;
import org.chiba.web.flux.FluxFacade;
import org.chiba.web.flux.IdegaFluxAdapter;
import org.chiba.web.session.XFormsSession;
import org.chiba.web.session.XFormsSessionManager;
import org.chiba.xml.xforms.exception.XFormsException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import com.idega.chiba.ChibaUtils;
import com.idega.chiba.web.exception.SessionExpiredException;
import com.idega.chiba.web.session.impl.IdegaXFormSessionManagerImpl;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.servlet.filter.RequestResponseProvider;
import com.idega.util.CoreUtil;
import com.idega.util.FileUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.RequestUtil;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

@Scope("request")
@Service(IdegaFluxFacade.BEAN_NAME)
public class IdegaFluxFacade extends FluxFacade {

	private static final Logger LOGGER = Logger.getLogger(IdegaFluxFacade.class.getName());

	public static final String BEAN_NAME = "fluxexhand";

	private HttpSession session;

	private String sessionKey = null, error = null;

	public String getCurrentXFormSessionKey() {
		return sessionKey;
	}

	public void setError(String error) {
		this.error = error;
	}

	public IdegaFluxFacade() {
		super();

		try {
			RequestResponseProvider provider = ELUtil.getInstance().getBean(RequestResponseProvider.class);
			this.session = provider.getRequest().getSession(Boolean.TRUE);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error while trying to get HTTP session object", e);
		}
	}

	public Element fireAction(String id, String sessionKey, String uri) throws FluxException {
		try {
			ChibaUtils.getInstance().prepareForChibaMethod(session, sessionKey);
			ChibaUtils.getInstance().onActionFired(sessionKey, uri);

			this.sessionKey = sessionKey;

			Element element = super.fireAction(id, sessionKey);

			if (error != null && error.equals(sessionKey + uri))
				throw new SessionExpiredException("Error firing action on " + id + ", session: " + sessionKey, ChibaUtils.getInstance().getSessionExpiredLocalizedString());

			return element;
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
					.concat(sessionKey).concat(ChibaUtils.getInstance().getSessionInformation(sessionKey)), e, ChibaUtils.getInstance().getSessionExpiredLocalizedString());
		} finally {
			ChibaUtils.getInstance().addXFormValue(sessionKey, id, value);
		}
	}

	public String getNodesetValue(String nodeset, String sessionKey) throws FluxException {
		try {
			return getAdapter(sessionKey).getChibaBean().getContainer()
			        .getModel("submission_model").getInstance("data-instance")
			        .getNodeValue(nodeset);

		} catch (XFormsException e) {
			throw new RuntimeException(e);
		}
	}

	private IdegaFluxAdapter getAdapter(String sessionKey) throws FluxException {

		XFormsSessionManager manager = (XFormsSessionManager) session
		        .getAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER);
		if (manager == null) {
			throw new FluxException("session timed out");
		}
		XFormsSession xFormsSession = manager.getXFormsSession(sessionKey);
		if (xFormsSession == null) {
			throw new FluxException(
			        "Your session has expired - Please start again.");
		}

		IdegaFluxAdapter adapter = (IdegaFluxAdapter) xFormsSession
		        .getAdapter();
		if (adapter != null) {

			return adapter;

		} else {
			// session expired or cookie got lost
			throw new FluxException("Session expired. Please start again.");
		}
	}

	@Override
	public Element setRepeatIndex(String id, String position, String sessionKey)
	        throws FluxException {
		try {
			ChibaUtils.getInstance().prepareForChibaMethod(session, sessionKey);

			return super.setRepeatIndex(id, position, sessionKey);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error setting repeat index", e);
			throw new SessionExpiredException(
			        "Unable to set repeat index for element: '"
			                .concat(id)
			                .concat("', position: '")
			                .concat(position)
			                .concat("' using session: ")
			                .concat(sessionKey)
			                .concat(
			                    ChibaUtils.getInstance().getSessionInformation(
			                        sessionKey)), e, ChibaUtils.getInstance()
			                .getSessionExpiredLocalizedString());
		}
	}

	@Override
	public Element fetchProgress(String id, String filename, String sessionKey) {
		try {
			ChibaUtils.getInstance().prepareForChibaMethod(session, sessionKey);

			if (ChibaUtils.getInstance().isUploadInvalid(session, sessionKey)) {
				IWContext iwc = CoreUtil.getIWContext();
				IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(IWBundleStarter.BUNDLE_IDENTIFIER).getResourceBundle(iwc);
				long uploadLimit = Long.valueOf(IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty("xform_upload_limit"));
				String messageToClient = iwrb.getLocalizedString("chiba.uploading_failed_check_file_size",
						"Sorry, uploading has failed. Please verify if file size is not exceeding " + FileUtil.getHumanReadableSize(uploadLimit) +
						" and try again.");

				throw ChibaUtils.getInstance().getIdegaChibaException(iwc.isIE() ? messageToClient : "Upload is marked as invalid",
						messageToClient, true);
			}

			return super.fetchProgress(id, filename, sessionKey);
		} catch (Exception e) {
			String message = "Exception while fetching progress for element: '"
			        .concat(id)
			        .concat("', file: '")
			        .concat(filename)
			        .concat("' using session: ")
			        .concat(sessionKey)
			        .concat(
			            ChibaUtils.getInstance().getSessionInformation(
			                sessionKey));
			LOGGER.log(Level.SEVERE, message, e);
			CoreUtil.sendExceptionNotification(message, e);

			throw ChibaUtils.getInstance().getIdegaChibaException(sessionKey,
			    e.getMessage(), "chiba.uploading_failed",
			    "Sorry, uploading failed. Please try again.");
		}
	}

	@Override
	public void keepAlive(String sessionKey) {
		try {
			ChibaUtils.getInstance().prepareForChibaMethod(session, sessionKey);

			super.keepAlive(sessionKey);
		} catch (Exception e) {
			String message = "Exception at keep alive, session key=".concat(
			    sessionKey).concat(
			    ChibaUtils.getInstance().getSessionInformation(sessionKey));
			LOGGER.log(Level.SEVERE, message, e);
			CoreUtil.sendExceptionNotification(message, e);

			throw ChibaUtils.getInstance().getIdegaChibaException(sessionKey,
			    e.getMessage(),
			    ChibaUtils.getInstance().getInternalErrorLocalizedString());
		}
	}

	@Override
	public void close(String sessionKey) {
		boolean error = false;
		String windowKey = null;
		try {
			if (!StringUtil.isEmpty(sessionKey) && sessionKey.indexOf("@") != -1) {
				String[] info = sessionKey.split("@");
				sessionKey = info[0];
				if (info.length >= 2) {
					windowKey = info[1];
				}
			}

			ChibaUtils.getInstance().markXFormSessionFinished(sessionKey, Boolean.TRUE);
			ChibaUtils.getInstance().onSessionClosed(sessionKey);

			super.close(sessionKey);
		} catch (Exception e) {
			error = true;
			String message = "Exception at close, session key=".concat(sessionKey).concat(ChibaUtils.getInstance().getSessionInformation(sessionKey));
			LOGGER.log(Level.SEVERE, message, e);
			CoreUtil.sendExceptionNotification(message, e);
		} finally {
			if (!error) {
				printSessionEndInfo(sessionKey, windowKey);
			}

			ChibaUtils.getInstance().getEmptyXFormValues(sessionKey);
		}
	}

	private void printSessionEndInfo(String sessionKey, String windowKey) {
		IWTimestamp browserWindowOpenedAt = null;
		if (!StringUtil.isEmpty(windowKey)) {
			browserWindowOpenedAt = new IWTimestamp(Long.valueOf(windowKey));
		}
		String message = "XForm session (" + sessionKey
		        + ") was removed because browser window (" + windowKey
		        + ") was closed.";
		if (browserWindowOpenedAt != null) {
			message += " Browser window was opened at: "
			        + browserWindowOpenedAt.getTimestamp().toString();
		}

		IWContext iwc = CoreUtil.getIWContext();
		if (iwc != null) {
			String language = RequestUtil.getBrowserLanguage(iwc.getRequest());
			if (!StringUtil.isEmpty(language)) {
				message += ". Browser language: " + language;
			}
		}

		LOGGER.info(message);
	}

	public int getNumberOfActiveSessions() {
		return IdegaXFormSessionManagerImpl.getXFormsSessionManager()
		        .getSessionCount();
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

	public void sendInformationAboutXFormsSessions(String receiverEmail) {
		ChibaUtils.getInstance().sendInformationAboutXForms(receiverEmail);
	}
}