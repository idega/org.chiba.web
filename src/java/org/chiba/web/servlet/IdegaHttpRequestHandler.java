package org.chiba.web.servlet;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.chiba.xml.xforms.ChibaBean;

import com.idega.util.CoreUtil;

public class IdegaHttpRequestHandler extends HttpRequestHandler {

	private static final Logger LOGGER = Logger.getLogger(IdegaHttpRequestHandler.class.getName());
	
	public IdegaHttpRequestHandler(ChibaBean chibaBean) {
		super(chibaBean);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map[] parseRequest(HttpServletRequest request) throws FileUploadException, UnsupportedEncodingException {
		try {
			return super.parseRequest(request);
		} catch (Exception e) {
			CoreUtil.sendExceptionNotification(e);
			LOGGER.log(Level.WARNING, "Error parsing request", e);
		}
		return new Map[4];
	}

	
}