package com.idega.chiba.web.xml.xforms.connector.webdav;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.faces.context.FacesContext;

import org.chiba.web.IWBundleStarter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.idega.idegaweb.IWMainApplication;
import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;
import com.idega.util.StringUtil;
import com.idega.util.xml.XPathUtil;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.3 $
 *
 * Last modified: $Date: 2008/03/26 07:44:20 $ by $Author: arunas $
 */
 
public class FileUploads extends FileUploadManager {

    public static final String UPLOADS_PATH = IWMainApplication.getIWMainApplication(FacesContext.getCurrentInstance()).getBundle(IWBundleStarter.BUNDLE_IDENTIFIER).getBundleBaseRealPath() + "/uploads/";
    private static final String ENTRIES = "./child::entry";
    private static final String VARIABLE_ELEMENT = "./descendant::node()[name(.) = $elementName]";
    private static final String ELEMENT_NAME = "elementName";
    private static final String VARIABLE = ".//entry[@filename]";

    public void cleanup(Node instance) {

	HashSet<String> folders = getFilesFolders(instance);
	String pathDir = "";
	for (String folder : folders) {
	    pathDir = UPLOADS_PATH + folder + CoreConstants.SLASH;
	    FileUtil.deleteNotEmptyDirectory(pathDir);
	}

    }

    public List<File> getFiles(String identifier, Node instance) {

	List<File> fileList = new ArrayList<File>();

	Element node = getUploadsElement(identifier, instance);
	
	XPathUtil util = new XPathUtil(ENTRIES);
	NodeList entries = util.getNodeset(node);

	for (int i = 0; i < entries.getLength(); i++) {
	
	    try {
		   URI uri = new URI (entries.item(i).getTextContent());
		   File file = new File(uri);
		   fileList.add(file);
	    } catch (URISyntaxException e) {
		 e.printStackTrace();
	    }
	 
	}
	
	return fileList;
    }

    protected Element getUploadsElement(String identifier, Node instance) {

	XPathUtil util = new XPathUtil(VARIABLE_ELEMENT);
	util.setVariable(ELEMENT_NAME, identifier);

	return (Element) util.getNode(instance);
    }

    protected HashSet<String> getFilesFolders(Node instance) {

	XPathUtil util = new XPathUtil(VARIABLE);
	Element entry = (Element) util.getNode(instance);

	HashSet<String> filesFolders = new HashSet<String>();
	List<String> valuesFromString = StringUtil.getValuesFromString(entry.getTextContent(), CoreConstants.SLASH);
	List<String> pathValues= StringUtil.getValuesFromString(UPLOADS_PATH, CoreConstants.SLASH);
	filesFolders.add(valuesFromString.get(pathValues.size()));

	return filesFolders;

    }

}