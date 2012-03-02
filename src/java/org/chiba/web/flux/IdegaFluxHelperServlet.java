package org.chiba.web.flux;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.chiba.adapter.ChibaEvent;
import org.chiba.adapter.DefaultChibaEventImpl;
import org.chiba.web.WebAdapter;
import org.chiba.web.session.XFormsSession;
import org.chiba.xml.xforms.config.Config;

import com.idega.chiba.ChibaUtils;
import com.idega.chiba.web.session.impl.IdegaXFormSessionManagerImpl;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.CoreUtil;
import com.idega.util.FileUtil;
import com.idega.util.IOUtil;

public class IdegaFluxHelperServlet extends FluxHelperServlet {

	private static final long serialVersionUID = -1460930997890952369L;

	private static final Logger LOGGER = Logger.getLogger(IdegaFluxHelperServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sessionKey = null;
		boolean isUpload = false;
		try {
			ChibaUtils.getInstance().prepareForChibaMethod(request);

			//	We do not want to remove XForm session silently! So, we had to copy this code from super class:
			WebAdapter webAdapter = null;
	        response.setContentType("text/html");

	        sessionKey = ChibaUtils.getInstance().getSessionKey(request);
	        XFormsSession xFormsSession = IdegaXFormSessionManagerImpl.getXFormsSessionManager().getXFormsSession(sessionKey);
	        webAdapter = xFormsSession.getAdapter();
	        if (webAdapter == null) {
	        	throw new ServletException(Config.getInstance().getErrorMessage("session-invalid"));
	        }
	        ChibaEvent chibaEvent = new DefaultChibaEventImpl();
	        chibaEvent.initEvent("http-request", null, request);
	        webAdapter.dispatch(chibaEvent);

	        isUpload = FileUpload.isMultipartContent(new ServletRequestContext(request));
	        if (isUpload) {
	        	long uploadLimit = Long.valueOf(IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty("xform_upload_limit", String.valueOf(1024 * 1024 * 10)));
	        	if (IOUtil.isUploadExceedingLimits(request, uploadLimit))
	        		throw new RuntimeException("Request size (" + FileUtil.getHumanReadableSize(IOUtil.getRequestSize(request)) + ") is exceeding the limit: " +
	        				FileUtil.getHumanReadableSize(uploadLimit));

	        	ServletOutputStream out = response.getOutputStream();
	            out.println("<html><head><title>status</title></head><body></body></html>");
	            out.close();
	        }
		} catch (Exception e) {
			String message = "Error while handling request: " + request.getRequestURI();
			LOGGER.log(Level.WARNING, message, e);
			CoreUtil.sendExceptionNotification(message, e);

			if (isUpload) {
				ChibaUtils.getInstance().markUploadAsFailed(request.getSession(true), sessionKey);
			}
		}
	}

}