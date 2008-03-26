package com.idega.chiba.web.xml.xforms.connector.webdav;

import java.io.File;
import java.util.List;

import org.w3c.dom.Node;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/03/26 07:44:35 $ by $Author: arunas $
 */

public abstract class FileUploadManager {
    
  /*  public FileUploadManager(Node instance) {	
    }
*/    
    public abstract List<File> getFiles(String identifier, Node instance) ;

}
