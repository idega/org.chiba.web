package com.idega.chiba.web.xml.xslt.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.chiba.xml.xslt.impl.Resource;

/**
 * A http resource.
 *
 * @author Vytautas Čivilis
 * @version $Id: HttpResource.java,v 1.1 2007/05/29 17:10:21 civilis Exp $
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
    public InputStream getInputStream() throws IOException {
        return url.openStream();
    }
}
