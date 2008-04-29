package com.idega.chiba.web.xml.xforms.connector.webdav;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.idega.idegaweb.IWBundle;
import com.idega.util.CoreConstants;
import com.idega.util.FileUtil;
import com.idega.util.StringUtil;
import com.idega.util.xml.XPathUtil;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.10 $
 *
 * Last modified: $Date: 2008/04/29 09:55:04 $ by $Author: arunas $
 */

public abstract class FileUploadManager {
	
    public static String UPLOADS_PATH;
    private static final String ENTRIES = "./child::entry";
    private static final String ENTRY_FILES = ".//entry[@filename]";
    
    final private XPathUtil entriesXPUT = new XPathUtil(ENTRIES);
    final private XPathUtil entryFilesXPUT = new XPathUtil(ENTRY_FILES);
      
    public void cleanup(Node instance) {

		Set<String> folders = getFilesFolders(instance);
		
		if(folders != null) {
		
			for (String folder : folders) {
				String pathDir = UPLOADS_PATH + folder + CoreConstants.SLASH;
				FileUtil.deleteNotEmptyDirectory(pathDir);
			}
		}
    }
    
    public static void initUPLOADSPATH(IWBundle bundle) {
		
    	UPLOADS_PATH = bundle.getBundleBaseRealPath() + "/uploads/";
	FileUtil.createFolder(UPLOADS_PATH);
	
    }
    
    public void cleanup (String filePath) {
	
	FileUtil.deleteNotEmptyDirectory(UPLOADS_PATH + filePath);
    }
    
    /**
     * upload file in directory
     * @param input 
     * @param fileName
     * @param pathDir
     */
    public void upload(InputStream input, String fileName, String pathDir) {
	
	 FileUtil.streamToFile(input, UPLOADS_PATH + pathDir, fileName);
    }
    
    public List<File> getFiles(String pathDir) {
	
	List<File> fileList = new ArrayList<File>();
	
	File[] files = FileUtil.getAllFilesInDirectory(UPLOADS_PATH + pathDir);
	
	for (int i = 0; i < files.length; i++)
	    fileList.add(files[i]);
	
	return fileList;
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
			List<String> pathValues = StringUtil.getValuesFromString(UPLOADS_PATH, CoreConstants.SLASH);
			
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