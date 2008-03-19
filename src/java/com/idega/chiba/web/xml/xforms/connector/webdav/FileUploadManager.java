package com.idega.chiba.web.xml.xforms.connector.webdav;

import java.util.List;

import org.w3c.dom.Node;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/03/19 11:40:19 $ by $Author: arunas $
 */

public abstract class FileUploadManager {
    
  /*  public FileUploadManager(Node instance) {	
    }
*/    
    public abstract List<FileItem> getFiles(String identifier, Node instance) ;

}
