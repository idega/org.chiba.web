/**
 * $Id: IWBundleStarter.java,v 1.17 2009/03/19 10:29:14 arunas Exp $
 * Created in 2006 by gediminas
 * 
 * Copyright (C) 2000-2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package org.chiba.web;

import java.util.Locale;

import org.chiba.xml.xforms.config.XFormsConfigException;
import org.chiba.xml.xslt.TransformerService;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplication;
import com.idega.servlet.filter.IWBundleResourceFilter;
import com.idega.util.CoreConstants;

/**
 * <p>
 * TODO gediminas Describe Type IWBundleStarter
 * </p>
 * Last modified: $Date: 2009/03/19 10:29:14 $ by $Author: arunas $
 * 
 * @author <a href="mailto:gediminas@idega.com">Gediminas Paulauskas</a>
 * @version $Revision: 1.17 $
 */
public class IWBundleStarter implements IWBundleStartable {
	
	public static final String CHIBA_CSS = "xforms.css";
	public static final String BUNDLE_IDENTIFIER = "org.chiba.web";
	public static final String BUNDLE_STYLES_PATH = "/style/";
	public static final String REPOSITORY_STYLES_PATH = CoreConstants.PUBLIC_PATH + BUNDLE_STYLES_PATH;
	public static final String CSS_STYLE_PATH = "resources/style/";
	
	public static final String SESSION_KEY = "sessionKey";
	
	public static final String TRANSFORMER_SERVICE = TransformerService.class.getName();
	
	public static WebFactory webFactory = new WebFactory();
	
	private Locale icelandicLocale = new Locale("is", "IS"); 

	public void start(IWBundle starterBundle) {
	
    	String chibaConfigURI = "/idegaweb/bundles/org.chiba.web.bundle/resources/chiba-config.xml";
    	String styleSheetsPath = "/idegaweb/bundles/org.chiba.web.bundle/resources/xslt/";
    	
//	TODO for xforms decimal formating
    	if (!Locale.getDefault().equals(icelandicLocale)) 
	    	Locale.setDefault(icelandicLocale);
    	
		IWMainApplication application = starterBundle.getApplication();
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(application, chibaConfigURI);
    	
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(application, styleSheetsPath+"html4.xsl");
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(application, styleSheetsPath+"ui.xsl");
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(application, styleSheetsPath+"html-form-controls.xsl");
    	
        webFactory.setServletContext(application.getServletContext());
        try {
            webFactory.initConfiguration();
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