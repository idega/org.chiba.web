package com.idega.chiba.web.exception;

import org.chiba.web.flux.FluxException;

import com.idega.chiba.web.session.impl.IdegaXFormSessionManagerImpl;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2009/06/02 16:18:50 $ by $Author: valdas $
 */

public class SessionExpiredException extends FluxException {

	private static final long serialVersionUID = 3701124290017696348L;

	private String messageToClient;

	private boolean reloadPage = Boolean.TRUE;

	public SessionExpiredException(String msg, Throwable exception, String xformSessionId) {
		this(msg, exception, CoreConstants.MINUS, xformSessionId);
	}

	public SessionExpiredException(String msg, String messageToClient, String xformSessionId) {
		this(msg, null, messageToClient, xformSessionId);
	}

	public SessionExpiredException(String msg, Throwable exception, String messageToClient, String xformSessionId) {
		super("Session has expired! ".concat(msg).concat(" XForm destroy information: ")
				.concat(((IdegaXFormSessionManagerImpl) IdegaXFormSessionManagerImpl.getXFormsSessionManager())
						.doRemoveXFormDestroyInfo(xformSessionId)));

		if (exception != null) {
			setStackTrace(exception.getStackTrace());
			CoreUtil.sendExceptionNotification(this);
		}

		this.messageToClient = messageToClient;
	}

	public String getMessageToClient() {
		return messageToClient;
	}

	public void setMessageToClient(String messageToClient) {
		this.messageToClient = messageToClient;
	}

	public boolean isReloadPage() {
		return reloadPage;
	}
}