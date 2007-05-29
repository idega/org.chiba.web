package com.idega.chiba.web.xml.xslt.impl;

import java.io.IOException;
import java.net.URI;

import org.chiba.xml.xslt.impl.Resource;
import org.chiba.xml.xslt.impl.ResourceResolver;

/**
 * Resolves http resources.
 *
 * @author Vytautas Čivilis
 * @version $Id: HttpResourceResolver.java,v 1.1 2007/05/29 17:10:21 civilis Exp $
 */
public class HttpResourceResolver implements ResourceResolver {

    /**
     * Resolves http resources.
     *
     * @param uri the URI denoting a resource.
     * @return the resource specified by the URI or <code>null</code> if this
     * resolver can't handle the URI.
     * @throws IOException if an error occurred during resolution.
     */
    public Resource resolve(URI uri) throws IOException {
    	
    	if (!uri.getScheme().equals("http"))
            return null;

        return new HttpResource(uri.toURL());
    }
}
