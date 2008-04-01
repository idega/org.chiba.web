/**
 * $Id: IWBundleStarter.java,v 1.8 2008/04/01 14:27:38 civilis Exp $
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

import com.idega.chiba.web.xml.xforms.connector.webdav.FileUploadManager;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.include.GlobalIncludeManager;
import com.idega.servlet.filter.IWBundleResourceFilter;

/**
 * <p>
 * TODO gediminas Describe Type IWBundleStarter
 * </p>
 * Last modified: $Date: 2008/04/01 14:27:38 $ by $Author: civilis $
 * 
 * @author <a href="mailto:gediminas@idega.com">Gediminas Paulauskas</a>
 * @version $Revision: 1.8 $
 */
public class IWBundleStarter implements IWBundleStartable {
	
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
    	
//    	creates uploads dir if doesn't exist
    	FileUploadManager.initUPLOADSPATH(starterBundle);
    	
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