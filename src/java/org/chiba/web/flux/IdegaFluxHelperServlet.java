package org.chiba.web.flux;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.idega.chiba.ChibaUtils;
import com.idega.util.CoreUtil;

public class IdegaFluxHelperServlet extends FluxHelperServlet {

	private static final long serialVersionUID = -1460930997890952369L;
	
	private static final Logger LOGGER = Logger.getLogger(IdegaFluxHelperServlet.class.getName());
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ChibaUtils.getInstance().prepareForChibaMethod(request);
		
		try {
			super.doPost(request, response);
		} catch (Exception e) {
			String message = "Error while handling request: " + request.getRequestURI();
			LOGGER.log(Level.WARNING, message, e);
			CoreUtil.sendExceptionNotification(message, e);
		}
	}

}
