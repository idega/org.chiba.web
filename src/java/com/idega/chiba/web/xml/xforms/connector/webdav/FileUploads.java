package com.idega.chiba.web.xml.xforms.connector.webdav;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.idega.util.xml.XPathUtil;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/03/27 10:15:09 $ by $Author: arunas $
 */
 
public class FileUploads extends FileUploadManager {
    private static final String VARIABLE_ELEMENT = "./descendant::node()[name(.) = $elementName]";
    private static final String ELEMENT_NAME = "elementName";
    @Override
    protected Element getUploadsElement(String identifier, Node instance) {

	XPathUtil util = new XPathUtil(VARIABLE_ELEMENT);
	util.setVariable(ELEMENT_NAME, identifier);

	return (Element) util.getNode(instance);
    }



}