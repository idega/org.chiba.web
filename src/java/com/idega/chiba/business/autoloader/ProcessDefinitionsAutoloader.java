package com.idega.chiba.business.autoloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpException;
import org.chiba.web.IWBundleStarter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainSlideStartedEvent;
import com.idega.presentation.IWContext;
import com.idega.slide.business.IWSlideService;
import com.idega.slide.util.WebdavExtendedResource;

/**
 * @author <a href="mailto:anton@idega.com">Anton Makarov</a>
 * @version $Revision: 1.1 $
 * 
 * Last modified: $Date: 2008/03/31 11:39:54 $ by $Author: anton $
 */
@Scope("singleton")
@Service
public class ProcessDefinitionsAutoloader implements ApplicationListener {

	private final Logger logger;

	public ProcessDefinitionsAutoloader() {
		logger = Logger.getLogger(getClass().getName());
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {	
//		if(applicationEvent instanceof IWMainSlideStartedEvent) {			
//			
//			try {
//				WebdavExtendedResource webdav_resource = getWebdavExtendedResource(IWBundleStarter.SLIDE_STYLES_PATH);
//				if (!webdav_resource.exists()) {
//					IWBundle bundle = ((IWMainApplication) IWContext.getInstance()
//							.getApplication()).getBundle(IWBundleStarter.BUNDLE_IDENTIFIER);
//					InputStream is = bundle
//							.getResourceInputStream(IWBundleStarter.BUNDLE_STYLES_PATH);
//
//					IWSlideService service_bean = getIWSlideService();
//
//					service_bean.uploadFileAndCreateFoldersFromStringAsRoot("files/public/style/", "xforms.css", is, "text/css", false);
//				}
//			} catch (IBOLookupException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (HttpException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}

	private IWSlideService getIWSlideService() throws IBOLookupException {
		try {
			return (IWSlideService) IBOLookup.getServiceInstance(getIWApplicationContext(), IWSlideService.class);
		} catch (IBOLookupException e) {
			logger.log(Level.SEVERE, "Error getting IWSlideService");
			throw e;
		}
	}

	private IWApplicationContext getIWApplicationContext() {
		IWApplicationContext iwac = IWMainApplication
				.getDefaultIWApplicationContext();
		return iwac;
	}

	private WebdavExtendedResource getWebdavExtendedResource(String path) throws HttpException, IOException, RemoteException, IBOLookupException {
		IWSlideService service = getIWSlideService();
		return service.getWebdavExtendedResource(path, service.getRootUserCredentials());
	}
}