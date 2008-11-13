package com.idega.chiba.web.xml.xforms.connector.xslt;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;

import org.chiba.adapter.ui.UIGenerator;
import org.chiba.adapter.ui.XSLTGenerator;
import org.chiba.web.IWBundleStarter;
import org.chiba.web.WebFactory;
import org.chiba.web.servlet.HttpRequestHandler;
import org.chiba.xml.xforms.config.Config;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xslt.TransformerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.block.web2.business.Web2Business;

@Service
@Scope("singleton")
public class UIGeneratorFactory {
	
	@Autowired private Web2Business web2Business;

	/**
	 * copied from chiba's XFormsSessionBase
	 * 
	 * creates and configures a UIGenerator that transcodes the XHTML/XForms
	 * document into the desired target format.
	 * 
	 * todo: make configuration of xsl file more flexible
	 * 
	 * @return an instance of an UIGenerator
	 * @throws URISyntaxException
	 * @throws XFormsException
	 */
	public UIGenerator createUIGenerator(
			TransformerService transformerService, ServletContext servletContext)
			throws URISyntaxException, XFormsException {

		Config configuration = Config.getInstance();

		String xsltPath = configuration
				.getProperty(WebFactory.XSLT_PATH_PROPERTY);
		// String relativeUris =
		// configuration.getProperty(WebFactory.RELATIVE_URI_PROPERTY);

		// todo: should the following two be removed? Use fixed resources dir as
		// convention now - user shouldn't need to touch that
		String scriptPath = configuration
				.getProperty(WebFactory.SCRIPT_PATH_PROPERTY);
		String cssPath = configuration
				.getProperty(WebFactory.CSS_PATH_PROPERTY);

		// todo: extract method
		// String xslFile = request.getParameter(XSL_PARAM_NAME);
		// if (xslFile == null){
		// xslFile =
		// configuration.getProperty(WebFactory.XSLT_DEFAULT_PROPERTY);
		// }

		String xslFile = configuration
				.getProperty(WebFactory.XSLT_DEFAULT_PROPERTY);

		// TransformerService transformerService = (TransformerService)
		// httpSession.getServletContext().getAttribute(TransformerService.class.getName());
		URI uri = new File(WebFactory.resolvePath(xsltPath, servletContext))
				.toURI().resolve(new URI(xslFile));
		
		// todo:make configurable
		XSLTGenerator generator = new XSLTGenerator();
		generator.setTransformerService(transformerService);
		generator.setStylesheetURI(uri);

		// if(relativeUris.equals("true"))
		// generator.setParameter("contextroot", ".");
		// else
		// generator.setParameter("contextroot", request.getContextPath());

		// generator.setParameter("sessionKey", getKey());
		// if(getProperty(XFormsSession.KEEPALIVE_PULSE) != null){
		// generator.setParameter("keepalive-pulse",getProperty(XFormsSession.KEEPALIVE_PULSE));
		// }

		// if(isScripted()){
		// generator.setParameter("action-url", getActionURL(true));
		// }else{
		// generator.setParameter("action-url", getActionURL(false));
		// }
		// generator.setParameter("debug-enabled",
		// String.valueOf(LOGGER.isDebugEnabled()));
		
		generator.setParameter("debug-enabled", false);
		String selectorPrefix = Config.getInstance().getProperty(
				HttpRequestHandler.SELECTOR_PREFIX_PROPERTY,
				HttpRequestHandler.SELECTOR_PREFIX_DEFAULT);
		generator.setParameter("selector-prefix", selectorPrefix);
		String removeUploadPrefix = Config.getInstance().getProperty(
				HttpRequestHandler.REMOVE_UPLOAD_PREFIX_PROPERTY,
				HttpRequestHandler.REMOVE_UPLOAD_PREFIX_DEFAULT);
		generator.setParameter("remove-upload-prefix", removeUploadPrefix);
		String dataPrefix = Config.getInstance().getProperty(
				"chiba.web.dataPrefix");
		generator.setParameter("data-prefix", dataPrefix);

		String triggerPrefix = Config.getInstance().getProperty(
				"chiba.web.triggerPrefix");
		generator.setParameter("trigger-prefix", triggerPrefix);

		// generator.setParameter("user-agent",
		// request.getHeader("User-Agent"));

		// generator.setParameter("scripted", String.valueOf(isScripted()));
		
		generator.setParameter("scripted", true);
		if (scriptPath != null) {
			generator.setParameter("scriptPath", scriptPath);
		}
		if (cssPath != null) {
			generator.setParameter("CSSPath", cssPath);
		}

		String compressedJS = Config.getInstance().getProperty(
				"chiba.js.compressed", "false");
		generator.setParameter("js-compressed", compressedJS);
		
		generator.setParameter("scriptPath", "/idegaweb/bundles/" + IWBundleStarter.BUNDLE_IDENTIFIER + ".bundle/resources/javascript/");
		generator.setParameter("imagesPath", "/idegaweb/bundles/" + IWBundleStarter.BUNDLE_IDENTIFIER + ".bundle/resources/style/images/");
		
		Web2Business web2 = getWeb2Business();
		
		try {
			generator.setParameter("uriToMootoolsLib", web2.getBundleURIToMootoolsLib());
			generator.setParameter("uriTojQueryLib", web2.getBundleURIToJQueryLib());
			generator.setParameter("uriToPrototypeLib", web2.getBundleURIToPrototypeLib());
			generator.setParameter("uriToScriptaculousLib", web2.getBundleURIToScriptaculousLib());
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return generator;
	}

	Web2Business getWeb2Business() {
		return web2Business;
	}
}