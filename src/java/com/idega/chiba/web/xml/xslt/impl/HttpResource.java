package com.idega.chiba.web.xml.xslt.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.Source;

import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xslt.impl.Resource;

/**
 * A http resource.
 *
 * @author Vytautas Čivilis
 * @version $Id: HttpResource.java,v 1.2 2008/03/21 15:56:48 anton Exp $
 */
public class HttpResource implements Resource {
    private URL url;

    /**
     * Creates a new http resource.
     *
     * @param url the http resource url.
     */
    public HttpResource(URL url) {
        this.url = url;
    }

    /**
     * @return 0 - always cache.
     */
    public long lastModified() {
        return 0;
    }

    /**
     * Returns the input stream of this resource.
     * @return the input stream of this resource.
     */
    public InputStream getInputStream() {
        try {
			return url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }

	public Source getSource() throws XFormsException {
		return null;
	}
}
