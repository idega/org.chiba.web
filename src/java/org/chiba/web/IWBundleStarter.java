/**
 * $Id: IWBundleStarter.java,v 1.10 2008/04/02 12:05:26 arunas Exp $
 * Created in 2006 by gediminas
 * 
 * Copyright (C) 2000-2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package org.chiba.web;

import org.chiba.xml.xforms.config.XFormsConfigException;
import org.chiba.xml.xslt.TransformerService;

import com.idega.chiba.web.xml.xforms.connector.webdav.FileUploadDaemon;
import com.idega.chiba.web.xml.xforms.connector.webdav.FileUploadManager;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplication;

import com.idega.servlet.filter.IWBundleResourceFilter;

/**
 * <p>
 * TODO gediminas Describe Type IWBundleStarter
 * </p>
 * Last modified: $Date: 2008/04/02 12:05:26 $ by $Author: arunas $
 * 
 * @author <a href="mailto:gediminas@idega.com">Gediminas Paulauskas</a>
 * @version $Revision: 1.10 $
 */
public class IWBundleStarter implements IWBundleStartable {
	
	public static final String CHIBA_CSS = "xforms.css";
	public static final String BUNDLE_IDENTIFIER = "org.chiba.web";
	public static final String BUNDLE_STYLES_PATH = "/style/";
	public static final String SLIDE_STYLES_PATH = "/files/public/style/";
	public static final String CSS_STYLE_PATH = "resources/style/";
	
    //private String defaultRequestEncoding = "UTF-8";

	public static final String TRANSFORMER_SERVICE = TransformerService.class.getName();
	
	public static WebFactory webFactory = new WebFactory();
	public FileUploadDaemon fileUplodDaemon;

//	public static final URI XSLT_URI = URI.create("bundle://" + BUNDLE_IDENTIFIER + "/resources/xslt/html4.xsl");
//	private static final URI CHIBA_CONFIG_URI = URI.create("bundle://" + BUNDLE_IDENTIFIER + "/resources/chiba-config.xml");
	
	public void start(IWBundle starterBundle) {
	
    	String chibaConfigURI = "/idegaweb/bundles/org.chiba.web.bundle/resources/chiba-config.xml";
    	String styleSheetsPath = "/idegaweb/bundles/org.chiba.web.bundle/resources/xslt/";
    	
		IWMainApplication application = starterBundle.getApplication();
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(application, chibaConfigURI);
    	
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(application, styleSheetsPath+"components.xsl");
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(application, styleSheetsPath+"html4.xsl");
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(application, styleSheetsPath+"ui.xsl");
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(application, styleSheetsPath+"html-form-controls.xsl");
    	
//    	creates uploads dir if doesn't exist
    	FileUploadManager.initUPLOADSPATH(starterBundle);
//	intializing and starting daemon
    	fileUplodDaemon = new FileUploadDaemon();
    	fileUplodDaemon.start();

//		GlobalIncludeManager.getInstance().addBundleStyleSheet(BUNDLE_IDENTIFIER, BUNDLE_STYLES_PATH);

        webFactory.setServletContext(application.getServletContext());
        try {
            webFactory.initConfiguration();
            //defaultRequestEncoding = webFactory.getConfig().getProperty("defaultRequestEncoding", defaultRequestEncoding);
            //webFactory.initLogging(this.getClass());
            webFactory.initTransformerService();
            webFactory.initXFormsSessionManager();
        } catch (XFormsConfigException e) {
            e.printStackTrace();
        }
	}

	public void stop(IWBundle starterBundle) {
		webFactory.destroyXFormsSessionManager();
		fileUplodDaemon.stop();
	}
}