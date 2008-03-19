package com.idega.chiba.web.xml.xforms.connector.webdav;

import java.io.File;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/03/19 11:40:19 $ by $Author: arunas $
 */
 
public class FileItem {
    private String name;
    private File file;
    
    public FileItem(String name, File file) {
	setName(name);
	setFile(file);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }
}
 