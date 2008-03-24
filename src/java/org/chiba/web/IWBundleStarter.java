/**
 * $Id: IWBundleStarter.java,v 1.6 2008/03/24 15:03:55 civilis Exp $
 * Created in 2006 by gediminas
 * 
 * Copyright (C) 2000-2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package org.chiba.web;

import java.util.logging.Logger;

import org.chiba.web.session.XFormsSessionManager;
import org.chiba.web.session.impl.DefaultXFormsSessionManagerImpl;
import org.chiba.xml.xforms.config.XFormsConfigException;
import org.chiba.xml.xslt.TransformerService;

import com.idega.chiba.web.session.impl.IdegaXFormSessionManagerImpl;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.include.GlobalIncludeManager;

import com.idega.servlet.filter.IWBundleResourceFilter;

/**
 * <p>
 * TODO gediminas Describe Type IWBundleStarter
 * </p>
 * Last modified: $Date: 2008/03/24 15:03:55 $ by $Author: civilis $
 * 
 * @author <a href="mailto:gediminas@idega.com">Gediminas Paulauskas</a>
 * @version $Revision: 1.6 $
 */
public class IWBundleStarter implements IWBundleStartable {
	
	private static final Logger log = Logger.getLogger(IWBundleStarter.class.getName());

	public static final String STYLE_SHEET_URL = "/style/xforms.css";
	public static final String BUNDLE_IDENTIFIER = "org.chiba.web";
	
    //private String defaultRequestEncoding = "UTF-8";

	public static final String TRANSFORMER_SERVICE = TransformerService.class.getName();
	
	public static WebFactory webFactory = new WebFactory();

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
		
		GlobalIncludeManager.getInstance().addBundleStyleSheet(BUNDLE_IDENTIFIER, STYLE_SHEET_URL);

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
	}
}