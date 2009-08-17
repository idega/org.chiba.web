package com.idega.chiba.web.exception;

import org.chiba.web.flux.FluxException;

import com.idega.util.CoreUtil;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2009/06/02 16:18:50 $ by $Author: valdas $
 */

public class SessionExpiredException extends FluxException {

	private static final long serialVersionUID = 3701124290017696348L;

	public SessionExpiredException() {
		super();
	}
	
	public SessionExpiredException(String msg, Throwable exception) {
		super(msg);
		setStackTrace(exception.getStackTrace());
		
		CoreUtil.sendExceptionNotification(this);
	}
}