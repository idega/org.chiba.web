/**
 * $Id: IWBundleStarter.java,v 1.7 2008/03/31 11:39:43 anton Exp $
 * Created in 2006 by gediminas
 * 
 * Copyright (C) 2000-2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package org.chiba.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpException;
import org.chiba.xml.xforms.config.XFormsConfigException;
import org.chiba.xml.xslt.TransformerService;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.DefaultIWBundle;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.include.GlobalIncludeManager;

import com.idega.presentation.IWContext;
import com.idega.servlet.filter.IWBundleResourceFilter;
import com.idega.slide.business.IWSlideService;
import com.idega.slide.util.WebdavExtendedResource;

/**
 * <p>
 * TODO gediminas Describe Type IWBundleStarter
 * </p>
 * Last modified: $Date: 2008/03/31 11:39:43 $ by $Author: anton $
 * 
 * @author <a href="mailto:gediminas@idega.com">Gediminas Paulauskas</a>
 * @version $Revision: 1.7 $
 */
public class IWBundleStarter implements IWBundleStartable {
	
	private static final Logger log = Logger.getLogger(IWBundleStarter.class.getName());

	public static final String BUNDLE_STYLES_PATH = "/style/xforms.css";
	public static final String SLIDE_STYLES_PATH = "/files/public/style/xforms.css";
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
    	
//    	saveToSlide("xforms.css");
		
		GlobalIncludeManager.getInstance().addBundleStyleSheet(BUNDLE_IDENTIFIER, BUNDLE_STYLES_PATH);

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
	
//	private void saveToSlide(String fileName) {
//		
//		try {
//			WebdavExtendedResource webdav_resource = getWebdavExtendedResource(SLIDE_STYLES_PATH);
//			if(!webdav_resource.exists()) {
//				IWBundle bundle = ((IWMainApplication) IWContext.getInstance().getApplication()).getBundle(BUNDLE_IDENTIFIER );
//				InputStream is = bundle.getResourceInputStream(BUNDLE_STYLES_PATH);
//				
//				IWSlideService service_bean = getIWSlideService();
//				
//				service_bean.uploadFileAndCreateFoldersFromStringAsRoot("files/public/style/", "xforms.css", is, "text/css", false);
//			}
//		} catch (IBOLookupException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (HttpException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	private IWSlideService getIWSlideService() throws IBOLookupException {
//		
//		try {
//			return (IWSlideService) IBOLookup.getServiceInstance(getIWApplicationContext(), IWSlideService.class);
//		} catch (IBOLookupException e) {
//			log.log(Level.SEVERE, "Error getting IWSlideService");
//			throw e;
//		}
//	}
//	
//	private IWApplicationContext getIWApplicationContext(){
//		IWApplicationContext iwac = IWMainApplication.getDefaultIWApplicationContext();		
//	    return iwac;
//	  }
//	
//	private WebdavExtendedResource getWebdavExtendedResource(String path) throws HttpException, IOException, RemoteException, IBOLookupException {
//		IWSlideService service = getIWSlideService();
//		return service.getWebdavExtendedResource(path, service.getRootUserCredentials());
//	}
}