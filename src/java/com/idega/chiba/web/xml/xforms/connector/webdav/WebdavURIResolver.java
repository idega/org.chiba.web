package com.idega.chiba.web.xml.xforms.connector.webdav;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.chiba.xml.xforms.connector.AbstractConnector;
import org.chiba.xml.xforms.connector.URIResolver;
import org.chiba.xml.xforms.exception.XFormsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.idega.repository.RepositoryService;
import com.idega.repository.jcr.JCRItem;
import com.idega.util.expression.ELUtil;

/**
 * This class resolves <code>webdav</code> URIs. It treats the denoted
 * <code>webdav</code> resource as XML and returns the parsed response.
 * <p/>
 * If the specified URI contains a fragment part, the specified element is
 * looked up via the <code>getElementById</code>. Thus, the parsed response must
 * have an internal DTD subset specifiyng all ID attributes. Otherwise the
 * element would not be found.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: WebdavURIResolver.java,v 1.1 2007/03/15 10:23:46 civilis Exp $
 */
public class WebdavURIResolver extends AbstractConnector implements URIResolver {

    /**
     * The logger.
     */
    private static Logger LOGGER = Logger.getLogger(WebdavURIResolver.class.getName());

    @Autowired
    private RepositoryService repository;

    /**
     * Performs link traversal of the <code>webdav</code> URI and returns the
     * result as a DOM document.
     *
     * @return a DOM node parsed from the <code>webdav</code> URI.
     * @throws XFormsException if any error occurred during link traversal.
     */
    @Override
	public Object resolve() throws XFormsException {
        try {
            // create uri
            URI uri = new URI(getURI());

            String path = uri.getPath();

            JCRItem resource = getRepositoryService().getRepositoryItem(path);

			if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("loading webdav resource '" + path + "'");
            }

            // parse file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            Document document = factory.newDocumentBuilder().parse(resource.getInputStream());

            // check for fragment identifier
            if (uri.getFragment() != null) {
                return document.getElementById(uri.getFragment());
            }

            return document;
        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    protected RepositoryService getRepositoryService() {
		if (repository == null)
			ELUtil.getInstance().autowire(this);
		return repository;
	}

}