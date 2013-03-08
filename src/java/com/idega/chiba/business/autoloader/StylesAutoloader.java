package com.idega.chiba.business.autoloader;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.chiba.web.IWBundleStarter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.business.DefaultSpringBean;
import com.idega.core.file.util.MimeTypeUtil;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.RepositoryStartedEvent;
import com.idega.util.IOUtil;

/**
 * @author <a href="mailto:anton@idega.com">Anton Makarov</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/04/04 14:52:22 $ by $Author: anton $
 */
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class StylesAutoloader extends DefaultSpringBean implements ApplicationListener<RepositoryStartedEvent> {

	private final Logger logger;

	public StylesAutoloader() {
		logger = Logger.getLogger(getClass().getName());
	}

	@Override
	public void onApplicationEvent(RepositoryStartedEvent applicationEvent) {
			IWMainApplication iwma = applicationEvent.getIWMA();
			IWApplicationContext iwac = iwma.getIWApplicationContext();

			String path = IWBundleStarter.REPOSITORY_STYLES_PATH + IWBundleStarter.CHIBA_CSS;

			if (getExistence(iwac, path)) {
				return;
			}

			IWBundle bundle = iwma.getBundle(IWBundleStarter.BUNDLE_IDENTIFIER);
			InputStream streamToBundleResource = null;
			try {
				streamToBundleResource = bundle.getResourceInputStream(IWBundleStarter.CSS_STYLE_PATH + IWBundleStarter.CHIBA_CSS);
				getRepositoryService().uploadFileAndCreateFoldersFromStringAsRoot(IWBundleStarter.REPOSITORY_STYLES_PATH, IWBundleStarter.CHIBA_CSS, streamToBundleResource,
						MimeTypeUtil.MIME_TYPE_CSS);
			} catch (Exception e) {
				logger.log(Level.WARNING, "Error uploading to " + path, e);
			} finally {
				IOUtil.close(streamToBundleResource);
			}
	}

	private boolean getExistence(IWApplicationContext iwac, String path) {
		try {
			return getRepositoryService().getExistence(path);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error checking existence for " + path, e);
		}
		return false;
	}
}