package com.idega.chiba.business.autoloader;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.chiba.web.IWBundleStarter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.file.util.MimeTypeUtil;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainSlideStartedEvent;
import com.idega.slide.business.IWSlideService;
import com.idega.util.IOUtil;

/**
 * @author <a href="mailto:anton@idega.com">Anton Makarov</a>
 * @version $Revision: 1.2 $
 * 
 * Last modified: $Date: 2008/04/04 14:52:22 $ by $Author: anton $
 */
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class StylesAutoloader implements ApplicationListener {

	private final Logger logger;

	public StylesAutoloader() {
		logger = Logger.getLogger(getClass().getName());
	}

	public void onApplicationEvent(ApplicationEvent applicationEvent) {	
		if (applicationEvent instanceof IWMainSlideStartedEvent) {
			IWMainApplication iwma = ((IWMainSlideStartedEvent)applicationEvent).getIWMA();
			IWApplicationContext iwac = iwma.getIWApplicationContext();
			
			String path = IWBundleStarter.SLIDE_STYLES_PATH + IWBundleStarter.CHIBA_CSS;
			InputStream streamToResource = null;
			try {
				streamToResource = getStreamToResource(iwac, path);
			} catch (Exception e) {}
			
			if (streamToResource == null) {
				IWBundle bundle = iwma.getBundle(IWBundleStarter.BUNDLE_IDENTIFIER);
				
				InputStream streamToBundleResource = null;
				try {
					streamToBundleResource = bundle.getResourceInputStream(IWBundleStarter.CSS_STYLE_PATH + IWBundleStarter.CHIBA_CSS);
					IWSlideService slideService = getIWSlideService(iwac);
					slideService.uploadFileAndCreateFoldersFromStringAsRoot(IWBundleStarter.SLIDE_STYLES_PATH, IWBundleStarter.CHIBA_CSS, streamToBundleResource,
							MimeTypeUtil.MIME_TYPE_CSS, Boolean.TRUE);
				} catch (Exception e) {
					logger.log(Level.WARNING, "Error uploading to " + path, e);
				} finally {
					IOUtil.close(streamToBundleResource);
				}
			}
			
			IOUtil.close(streamToResource);
		}
	}

	private IWSlideService getIWSlideService(IWApplicationContext iwac) throws IBOLookupException {
		try {
			return IBOLookup.getServiceInstance(iwac, IWSlideService.class);
		} catch (IBOLookupException e) {
			logger.log(Level.SEVERE, "Error getting IWSlideService");
			throw e;
		}
	}

	private InputStream getStreamToResource(IWApplicationContext iwac, String path) throws Exception {
		IWSlideService service = getIWSlideService(iwac);
		return service.getInputStream(path);
		//return service.getWebdavExtendedResource(path, service.getRootUserCredentials());
	}
}