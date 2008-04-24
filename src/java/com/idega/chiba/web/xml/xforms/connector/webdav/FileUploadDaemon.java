package com.idega.chiba.web.xml.xforms.connector.webdav;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.idega.util.EventTimer;
import com.idega.util.FileUtil;
import com.idega.util.IWTimestamp;

/**
 * @author <a href="mailto:civilis@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/04/24 23:57:48 $ by $Author: laddi $
 */

public class FileUploadDaemon implements ActionListener {

    public static final String THREAD_NAME= "uploaded_file_Daemon";
    private EventTimer fileTimer;

    public void start() {
	// checking uploaded files every hour. 
	this.fileTimer = new EventTimer(EventTimer.THREAD_SLEEP_1_HOUR,THREAD_NAME);
	this.fileTimer.addActionListener(this);
	// Starts the thread after 5 mins.
	this.fileTimer.start(EventTimer.THREAD_SLEEP_5_MINUTES);
    }

    public void actionPerformed(ActionEvent event) {
	try {
	    if (event.getActionCommand().equalsIgnoreCase(THREAD_NAME)) {
		System.out.println("[File Upload Daemon - "+ IWTimestamp.RightNow().toString()	+ "] - Deleting uploaded files older than 24h");
		// deleting uploaded files older than 24h.
		FileUtil.deleteAllFilesAndFolderInFolderOlderThan(FileUploadManager.UPLOADS_PATH, EventTimer.THREAD_SLEEP_24_HOURS);
	    }

	} catch (Exception x) {
	    x.printStackTrace();
	}
    }

    public void stop() {
	if (this.fileTimer != null) {
	    this.fileTimer.stop();
	    this.fileTimer = null;
	}

    }
}
