package com.idega.chiba.web.xml.xforms.connector.webdav;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.faces.context.FacesContext;

import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import org.chiba.web.IWBundleStarter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.idega.idegaweb.IWMainApplication;
import com.idega.util.FileUtil;
import com.idega.util.xml.XPathUtil;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/03/19 11:40:19 $ by $Author: arunas $
 */
 
public class FileUploads extends FileUploadManager {

    public static final String UPLOADS_PATH = IWMainApplication.getIWMainApplication(FacesContext.getCurrentInstance()).getBundle(IWBundleStarter.BUNDLE_IDENTIFIER).getBundleBaseRealPath() + "/uploads/";
    private static final String ENTRIES = "./child::entry";
    private static final String FILENAME = "filename";
    private static final String VARIABLE_ELEMENT = "./descendant::node()[name(.) = $elementName]";
    private static final String ELEMENT_NAME = "elementName";
    private static final String ENTRY = ".//entry[@filename]";

    public void cleanup(Node instance) {

	HashSet<String> folders = getFilesFolders(instance);
	String pathDir = "";
	for (String folder : folders) {
	    pathDir = UPLOADS_PATH + folder + CoreConstants.SLASH;
	    FileUtil.deleteNotEmptyDirectory(pathDir);
	}

    }

    public List<FileItem> getFiles(String identifier, Node instance) {

	List<FileItem> fileList = new ArrayList<FileItem>();

	Element node = getFirstElement(identifier, instance);

	XPathUtil util = new XPathUtil(ENTRIES);
	NodeList entry = util.getNodeset(node);

	for (int i = 0; i < entry.getLength(); i++) {

	    Element entryElment = (Element) entry.item(i);

	    List<String> valuesFromString = StringUtil.getValuesFromString(entryElment.getTextContent(), CoreConstants.SLASH);
	    int listSize = valuesFromString.size();
	    String path = UPLOADS_PATH + valuesFromString.get(listSize - 2) + CoreConstants.SLASH + valuesFromString.get(listSize - 1);

	    File file = new File(path);
	    FileItem filedata = new FileItem(entryElment.getAttribute(FILENAME), file);
	    fileList.add(filedata);
	}
	// impl by abstr
	return fileList;
    }

    protected Element getFirstElement(String identifier, Node instance) {

	XPathUtil util = new XPathUtil(VARIABLE_ELEMENT);
	util.setVariable(ELEMENT_NAME, identifier);

	return (Element) util.getNode(instance);
    }

    protected HashSet<String> getFilesFolders(Node instance) {

	XPathUtil util = new XPathUtil(ENTRY);
	Element entry = (Element) util.getNode(instance);

	HashSet<String> filesFolders = new HashSet<String>();
	List<String> valuesFromString = StringUtil.getValuesFromString(entry.getTextContent(), CoreConstants.SLASH);
	filesFolders.add(valuesFromString.get(valuesFromString.size() - 2));

	return filesFolders;

    }

}