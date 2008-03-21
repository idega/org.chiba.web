package com.idega.chiba.web.xml.xslt.impl;

import java.io.File;
import java.net.URI;
import org.chiba.xml.xslt.impl.FileResource;
import org.chiba.xml.xslt.impl.Resource;
import org.chiba.xml.xslt.impl.ResourceResolver;
import com.idega.idegaweb.DefaultIWBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;

public class ChibaBundleHackResourceResolver implements ResourceResolver {

	private String sBundlesDirectory;
	private IWMainApplication iwma;
	
	public ChibaBundleHackResourceResolver(IWMainApplication iwma) {
		this.iwma = iwma;
		this.sBundlesDirectory = System.getProperty(DefaultIWBundle.SYSTEM_BUNDLES_RESOURCE_DIR);
	}

	public Resource resolve(URI uri) {

		try {
			throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!uri.getScheme().equals("http")) {
        	return null;
		}

		String bundleIdentifier = uri.getHost();
		String pathWithinBundle = uri.getPath();
		
		if (pathWithinBundle.startsWith("/")) {
			pathWithinBundle = pathWithinBundle.substring(1); // drop leading slash
		}

		Resource res = null;

		if (this.sBundlesDirectory != null) {
			String filePath = sBundlesDirectory + File.separator + bundleIdentifier + File.separator + pathWithinBundle;
			File file = new File(filePath);
			res = new FileResource(file);
		}
		else { 
			IWBundle bundle = iwma.getBundle(bundleIdentifier);
			res = new ChibaBundleResource(bundle, pathWithinBundle);
		}

		return res;
	}
}