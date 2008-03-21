package com.idega.chiba.web.xml.xforms.connector.xslt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.chiba.web.IWBundleStarter;
import org.chiba.xml.xslt.TransformerService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.idega.idegaweb.IWMainApplication;


/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.3 $
 *
 * Last modified: $Date: 2008/03/21 15:57:11 $ by $Author: anton $
 */
public class XsltURIResolverUtil {

	private static Logger LOGGER = Logger.getLogger(XsltURIResolverUtil.class.getName());
	
	private static final String datasource_tag = "datasource";
	private static final String uri_att = "uri";
	private static final String xslt_postfix = ".xslt";
	private static final String xsl_postfix = ".xsl";
	
	/**
	 * <p>If the file is stylesheet (determined with .xslt or .xsl), then it's used as a transformer for an
	 * datasource xml existing somewhere accessible to xml format, formbuilder understands.
	 * </p>
	 *
	 * <p>The stylesheet file itself should contain tag datasource with an attribute uri, 
	 * which contains the full link to the xml the transformation is taken against.</p> 
	 * 
	 * @param document - stylesheet document
	 * @param builder - document builder, as it should be already retrieved to get document
	 * @param stylesheet_uri - sad, but chiba's transformer service accepts uri only so the stylesheet 
	 * document is retrieved actually two times, but it is always small one so that's not so big problem
	 * @return if uri provided ends with .xsl or .xslt, then the transformed document is returned OR empty, 
	 * if an error occured while transformed. else, if that's not stylesheet, the provided document is returned
	 * @throws TransformerException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Document findAndConvertXmlFromStylesheet(Document document, DocumentBuilder builder, URI stylesheet_uri) throws TransformerException, MalformedURLException, IOException, SAXException {
		
		String path = stylesheet_uri.getPath();
		
		if(!path.endsWith(xslt_postfix) && !path.endsWith(xsl_postfix))
			return document;
		
		NodeList ds = document.getElementsByTagName(datasource_tag);
        
        if(ds == null || ds.getLength() == 0) {
        	LOGGER.log(Level.WARNING, "datasource element not found in xslt document, ignoring stylesheet and returning empty nodeset");
        	return builder.newDocument();
        }
        
        String resource_uri = ((Element)ds.item(0)).getAttribute(uri_att);
        
        if(resource_uri == null || "".equals(resource_uri)) {
        	LOGGER.log(Level.WARNING, "datasource uri attribute was empty or didn't exist, ignoring stylesheet and returning empty nodeset");
        	return builder.newDocument();
        }
        
        URL resource_url = new URL(resource_uri);
        
        TransformerService transf_service = (TransformerService)IWMainApplication.getDefaultIWMainApplication().getAttribute(IWBundleStarter.TRANSFORMER_SERVICE);
        Transformer trans = transf_service.getTransformer(stylesheet_uri);
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        trans.transform(
        		new StreamSource(resource_url.openStream()), 
        		new StreamResult(output));
        
        return builder.parse(new ByteArrayInputStream(output.toByteArray()));
	}
}