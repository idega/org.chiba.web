package com.idega.chiba.web.xml.xslt.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;

import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xslt.impl.Resource;
import com.idega.idegaweb.IWBundle;

public class ChibaBundleResource implements Resource {

	private IWBundle bundle;
	private String pathWithinBundle;
	
	public ChibaBundleResource(IWBundle bundle, String pathWithinBundle) {
		this.bundle = bundle;
		this.pathWithinBundle = pathWithinBundle;
	}

	public InputStream getInputStream() {
		try {
			return bundle.getResourceInputStream(pathWithinBundle);
		} catch (IOException exp) {
			exp.printStackTrace();
			return null;
		}
	}

	public long lastModified() {
		return bundle.getResourceTime(pathWithinBundle);
	}

	public Source getSource() throws XFormsException {
		return null;
	}
}