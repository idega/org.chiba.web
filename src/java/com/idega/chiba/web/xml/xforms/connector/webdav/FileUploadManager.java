package com.idega.chiba.web.xml.xforms.connector.webdav;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/03/27 10:24:17 $ by $Author: arunas $
 */

public abstract class FileUploadManager {
    public static final String UPLOADS_PATH = IWMainApplication.getIWMainApplication(FacesContext.getCurrentInstance()).getBundle(IWBundleStarter.BUNDLE_IDENTIFIER).getBundleBaseRealPath() + "/uploads/";
    private static final String ENTRIES = "./child::entry";
    private static final String ENTRY_FILES = ".//entry[@filename]";
      
    public void cleanup(Node instance) {

	Set<String> folders = getFilesFolders(instance);
	String pathDir = "";
	for (String folder : folders) {
	    pathDir = FileUploads.UPLOADS_PATH + folder + CoreConstants.SLASH;
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
    
    protected Set<String> getFilesFolders(Node instance) {

	XPathUtil util = new XPathUtil(ENTRY_FILES);
	NodeList entries = util.getNodeset(instance);
	Set<String> filesFolders = new HashSet<String>();
	List<String> pathValues= StringUtil.getValuesFromString(FileUploads.UPLOADS_PATH, CoreConstants.SLASH);
	
	for (int i = 0; i < entries.getLength(); i++) {
	    	List<String> valuesFromString = StringUtil.getValuesFromString(entries.item(i).getTextContent(), CoreConstants.SLASH);
	    	filesFolders.add(valuesFromString.get(pathValues.size()));
	}
	
	return filesFolders;
	
    }
    
    protected abstract Element getUploadsElement(String identifier, Node instance);

}
