package org.chiba.web.servlet;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.chiba.xml.xforms.ChibaBean;

import com.idega.chiba.ChibaUtils;
import com.idega.util.CoreUtil;

public class IdegaHttpRequestHandler extends HttpRequestHandler {

	private static final Logger LOGGER = Logger.getLogger(IdegaHttpRequestHandler.class.getName());

	private String xformSessionKey;

	public IdegaHttpRequestHandler(ChibaBean chibaBean) {
		super(chibaBean);
	}

	@Override
	protected Map<?, ?>[] parseRequest(HttpServletRequest request) throws FileUploadException, UnsupportedEncodingException {
		try {
			Map<?, ?>[] result = super.parseRequest(request);
			return result;
		} catch (Exception e) {
			String error = "Error parsing request";
			CoreUtil.sendExceptionNotification(error, e);
			LOGGER.log(Level.WARNING, error, e);

			ChibaUtils.getInstance().markUploadAsFailed(xformSessionKey);
		}
		return new Map[4];
	}

	@Override
	protected Map<String, FileItem> parseUploadParameter(String name, FileItem item, @SuppressWarnings("rawtypes") Map uploads) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, FileItem> params = super.parseUploadParameter(name, item, uploads);
			return params;
		} catch (Exception e) {
			String error = "Error while parsing upload parameters";
			CoreUtil.sendExceptionNotification(error, e);
			LOGGER.log(Level.WARNING, error, e);

			ChibaUtils.getInstance().markUploadAsFailed(xformSessionKey);
		}
		return new HashMap<String, FileItem>();
	}

	@Override
	public void setSessionKey(String sessionKey) {
		super.setSessionKey(sessionKey);

		this.xformSessionKey = sessionKey;
	}
}