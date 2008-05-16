package com.idega.chiba.web.upload;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.chiba.xml.dom.DOMUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.idega.core.file.tmp.TmpFileResolverType;
import com.idega.core.file.tmp.TmpFileResolver;
import com.idega.util.CoreConstants;
import com.idega.util.xml.XPathUtil;


/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/05/16 15:13:33 $ by $Author: anton $
 */
@Scope("singleton")
@TmpFileResolverType("xformVariables")
@Service("xfAttResResolver")
public class XFormTmpFileResolverImpl implements TmpFileResolver {

	public static String UPLOADS_PATH;
	private static final String VARIABLE_NAME_VAR = "variableName";
	private final XPathUtil entriesXPUT;
    private final XPathUtil entryFilesXPath;
    private final XPathUtil byVariableNameXpath;
    
	
    public XFormTmpFileResolverImpl() {
    	entriesXPUT = new XPathUtil("./child::entry");
    	entryFilesXPath = new XPathUtil(".//entry[@filename]");
    	byVariableNameXpath = new XPathUtil(".//node()[@mapping = $variableName]");
	}

	public Collection<File> resolveFiles(String identifier, Object resource) {
		
		if(!(resource instanceof Node)) {
			
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Wrong resource provided. Expected of type "+Node.class.getName()+", but got "+resource.getClass().getName());
			return new ArrayList<File>(0);
		}
		
		Node instance = (Node)resource;
		Element node = getUploadsElement(identifier, instance);
		NodeList entries;
		
		synchronized (entriesXPUT) {
			entries = entriesXPUT.getNodeset(node);
		}
		
		if(entries != null) {
			
			ArrayList<File> files = new ArrayList<File>(entries.getLength());
			
			for (int i = 0; i < entries.getLength(); i++) {
				
				String uriStr = entries.item(i).getChildNodes().item(0).getTextContent();
		    	
		    	if(!CoreConstants.EMPTY.equals(uriStr) && !uriStr.startsWith(CoreConstants.NEWLINE)) {
		    		
		    		if(uriStr.startsWith("file:"))
		    			uriStr = uriStr.substring("file:".length());
		    		
		    		try {
						uriStr = URLDecoder.decode(uriStr, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					
		    		File f = new File(uriStr);
		    		files.add(f);
		    	}
			}
			
			return files;
		}
		
		return new ArrayList<File>(0);
	}
	
	public String getRealBasePath() {
		
		return UPLOADS_PATH;
	}
	
	public void setRealBasePath(String basePath) {
		
		UPLOADS_PATH = basePath;
	}
	
	public String getContextPath() {
		
		return "xformsAttachments/";
	}
	
	public Collection<File> getFilesToCleanUp(String identifier, Object resource) {
		
		if(!(resource instanceof Node)) {
			
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Wrong resource provided. Expected of type "+Node.class.getName()+", but got "+resource.getClass().getName());
			return new ArrayList<File>(0);
		}
		
		NodeList entries = entryFilesXPath.getNodeset((Node)resource);
		
		if(entries != null && entries.getLength() != 0) {
		
			ArrayList<File> filesToCleanup = new ArrayList<File>(entries.getLength());
			String realBasePath = getRealBasePath();
			
			for (int i = 0; i < entries.getLength(); i++) {
				
				String uriStr = entries.item(i).getTextContent();
				
				if(!CoreConstants.EMPTY.equals(uriStr)) {
					
					if(uriStr.contains(realBasePath)) {
						
						String[] spltd = uriStr.split(realBasePath);
						String leftover = spltd[spltd.length-1];
						String ff = leftover.substring(0, leftover.indexOf(CoreConstants.SLASH));
						
						if(ff.startsWith(CoreConstants.SLASH))
							ff = ff.substring(1);
						
						String complURI = realBasePath+ff;
						
						filesToCleanup.add(new File(complURI));
					}
				}
			}
			
			return filesToCleanup;
		}
		
		return new ArrayList<File>(0);
	}
	
	protected Element getUploadsElement(String identifier, Node context) {
		
		if(context instanceof Element && identifier.equals(((Element)context).getAttribute("mapping"))) {
			
			return (Element)context;
		}

		synchronized (byVariableNameXpath) {
		
			byVariableNameXpath.setVariable(VARIABLE_NAME_VAR, identifier);
			Element n = (Element)byVariableNameXpath.getNode(context);
			return n;
		}
	}
}