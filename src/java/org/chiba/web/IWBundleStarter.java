/**
 * $Id: IWBundleStarter.java,v 1.1 2007/03/15 10:23:42 civilis Exp $
 * Created in 2006 by gediminas
 * 
 * Copyright (C) 2000-2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package org.chiba.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.TransformerException;

import org.chiba.web.session.impl.DefaultXFormsSessionManagerImpl;
import org.chiba.xml.xforms.config.Config;
import org.chiba.xml.xforms.config.XFormsConfigException;
import org.chiba.xml.xslt.TransformerService;
import org.chiba.xml.xslt.impl.CachingTransformerService;
import org.chiba.xml.xslt.impl.ResourceResolver;

import com.idega.chiba.web.xml.xslt.impl.BundleResourceResolver;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.include.GlobalIncludeManager;

/**
 * <p>
 * TODO gediminas Describe Type IWBundleStarter
 * </p>
 * Last modified: $Date: 2007/03/15 10:23:42 $ by $Author: civilis $
 * 
 * @author <a href="mailto:gediminas@idega.com">Gediminas Paulauskas</a>
 * @version $Revision: 1.1 $
 */
public class IWBundleStarter implements IWBundleStartable {
	
	private static final Logger log = Logger.getLogger(IWBundleStarter.class.getName());

	private static final String STYLE_SHEET_URL = "/style/xforms.css";
	public static final String BUNDLE_IDENTIFIER = "org.chiba.web";

	public static final String TRANSFORMER_SERVICE = TransformerService.class.getName();

	public static final URI XSLT_URI = URI.create("bundle://" + BUNDLE_IDENTIFIER + "/resources/xslt/html4.xsl");
	private static final URI CHIBA_CONFIG_URI = URI.create("bundle://" + BUNDLE_IDENTIFIER + "/resources/chiba-config.xml");

	public void start(IWBundle starterBundle) {
		GlobalIncludeManager.getInstance().addBundleStyleSheet(BUNDLE_IDENTIFIER, STYLE_SHEET_URL);
		
		System.out.println("starting chiba ........_________...............");
		IWMainApplication application = starterBundle.getApplication();

		// create transformer service
		ResourceResolver resolver = new BundleResourceResolver(application);
		TransformerService transformerService = new CachingTransformerService(resolver);
		application.setAttribute(TRANSFORMER_SERVICE, transformerService);

    	// cache default stylesheet
        try {
			transformerService.getTransformer(XSLT_URI);
		}
		catch (TransformerException e) {
			log.log(Level.SEVERE, "Cannot load XForms transformer stylesheet", e);
		}
		
		createXFormsSessionManager(0, 0);
		
		// read chiba config
		try {
			InputStream inputStream = resolver.resolve(CHIBA_CONFIG_URI).getInputStream();
			Config.getInstance(inputStream);
		}
		catch (IOException e) {
			log.log(Level.SEVERE, "Error reading chiba config", e);
		}
		catch (XFormsConfigException e) {
			log.log(Level.SEVERE, "Error initializing chiba config", e);
		}
	}

	public void stop(IWBundle starterBundle) {
		starterBundle.getApplication().removeAttribute(TRANSFORMER_SERVICE);
		DefaultXFormsSessionManagerImpl manager = DefaultXFormsSessionManagerImpl.getInstance();
		manager.kill();
		manager.interrupt();
	}

	/**
     * factory method to create and setup an XFormsSessionManager. Overwrite this to provide your own implementation.
     *
     * @param wipingInterval
     * @param timeout
     */
    protected void createXFormsSessionManager(int wipingInterval, int timeout) {
        DefaultXFormsSessionManagerImpl manager = DefaultXFormsSessionManagerImpl.getInstance();
        
        manager.setInterval(wipingInterval);
        manager.setTimeout(timeout);

        //start running the session cleanup
        manager.start();
    }
}