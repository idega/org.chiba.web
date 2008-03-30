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
 * @version $Revision: 1.8 $
 *
 * Last modified: $Date: 2008/03/30 21:57:26 $ by $Author: civilis $
 */

public abstract class FileUploadManager {
	
    private static String UPLOADS_PATH;
    private static final String ENTRIES = "./child::entry";
    private static final String ENTRY_FILES = ".//entry[@filename]";
    
    final private XPathUtil entriesXPUT = new XPathUtil(ENTRIES);
    final private XPathUtil entryFilesXPUT = new XPathUtil(ENTRY_FILES);
      
    public void cleanup(Node instance) {

		Set<String> folders = getFilesFolders(instance);
		
		if(folders != null) {
		
			for (String folder : folders) {
				String pathDir = getUPLOADSPATH() + folder + CoreConstants.SLASH;
			    FileUtil.deleteNotEmptyDirectory(pathDir);
			}
		}
    }
    
    public static String getUPLOADSPATH() {
		
    	if(UPLOADS_PATH == null)
    		UPLOADS_PATH = IWMainApplication.getIWMainApplication(FacesContext.getCurrentInstance()).getBundle(IWBundleStarter.BUNDLE_IDENTIFIER).getBundleBaseRealPath() + "/uploads/";

    	return UPLOADS_PATH;
	}

    public List<File> getFiles(String identifier, Node instance) {

		List<File> fileList = new ArrayList<File>();
	
		Element node = getUploadsElement(identifier, instance);
		NodeList entries;
		
		synchronized (entriesXPUT) {
			entries = entriesXPUT.getNodeset(node);
		}
		
		if(entries != null) {
		
			for (int i = 0; i < entries.getLength(); i++) {
				
			    try {
			    	String uriStr = entries.item(i).getTextContent();
			    	
			    	if(!CoreConstants.EMPTY.equals(uriStr)) {
			    	
			    		URI uri = new URI (uriStr);
			    		File file = new File(uri);
			    		fileList.add(file);
			    	}
				   
			    } catch (URISyntaxException e) {
			    	e.printStackTrace();
			    }
			}
		}
		
		return fileList;
    }
    
    protected Set<String> getFilesFolders(Node instance) {
    	
    	NodeList entries;
		
		synchronized (entryFilesXPUT) {
			entries = entryFilesXPUT.getNodeset(instance);
		}
		
		if(entries != null && entries.getLength() != 0) {
		
			Set<String> filesFolders = new HashSet<String>();
			List<String> pathValues = StringUtil.getValuesFromString(getUPLOADSPATH(), CoreConstants.SLASH);
			
			for (int i = 0; i < entries.getLength(); i++) {
				
				String uriStr = entries.item(i).getTextContent();
				
				if(!CoreConstants.EMPTY.equals(uriStr)) {
					
					List<String> valuesFromString = StringUtil.getValuesFromString(uriStr, CoreConstants.SLASH);
			    	filesFolders.add(valuesFromString.get(pathValues.size()));
				}
			}
			
			return filesFolders;
		}

		return null;
    }
    
    protected abstract Element getUploadsElement(String identifier, Node instance);
}