package com.idega.chiba.web.xml.xforms.connector.webdav;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.xforms.connector.AbstractConnector;
import org.chiba.xml.xforms.connector.SubmissionHandler;
import org.chiba.xml.xforms.core.Submission;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.idegaweb.IWMainApplication;
import com.idega.slide.business.IWSlideService;

/**
 * The file submission driver serializes and submits instance data to a file.
 * <p/>
 * When using the <code>put</code> submission method, the driver only supports
 * the replace mode <code>none</code>. It simply serializes the instance data to
 * the file denoted by the connector URI. When this file exists, it will be
 * overwritten silently, otherwise it will be created.
 * <p/>
 * 
 * @author Gediminas Paulauskas
 * @version $Id: WebdavSubmissionHandler.java,v 1.3 2008/03/19 11:40:19 arunas Exp $
 */
public class WebdavSubmissionHandler extends AbstractConnector implements SubmissionHandler {
    
	private static final Logger LOGGER = Logger.getLogger(WebdavSubmissionHandler.class.getName());
	private static final String form_id_tag = "form_id";
	private static final String slash = "/";
	
	public static final String SUBMITTED_DATA_PATH = "/files/forms/submissions";
	

    /**
     * Serializes and submits the specified instance data over the
     * <code>webdav</code> protocol.
     *
     * @param submission the submission issuing the request.
     * @param instance the instance data to be serialized and submitted.
     * @return <code>null</code>.
     * @throws XFormsException if any error occurred during submission.
     */
    public Map submit(Submission submission, Node instance) throws XFormsException {
        if (submission.getMethod().equalsIgnoreCase("put")) {
            if (!submission.getReplace().equals("none") && !submission.getReplace().equals("all")) {
                throw new XFormsException("submission mode '" + submission.getReplace() + "' not supported");
            }
            
            System.out.println("submit: ");
            
            DOMUtil.prettyPrintDOM(instance);
        
            FileUploads fileUpload = new FileUploads();
            fileUpload.getFiles("File_upload_fbc_6", instance);
            fileUpload.cleanup(instance);
                   
            try {
                String form_id = getFormIdFromSubmissionInstance(instance);
                
                if(form_id != null) {
                	ByteArrayOutputStream out = new ByteArrayOutputStream();
    				serialize(submission, instance, out);
    				InputStream is = new ByteArrayInputStream(out.toByteArray());
    				saveSubmittedData(form_id, is);
                } else {
                	LOGGER.severe("Form id not found in the submitted data instance");
                }
            }
            catch (Exception e) {
                throw new XFormsException(e);
            }

            return null;
        }

        throw new XFormsException("submission method '" + submission.getMethod() + "' not supported");
    }
	
	protected String getFormIdFromSubmissionInstance(Node instance) {
		
		Element form_id = DOMUtil.getChildElement(instance, form_id_tag);
		
        if (form_id != null) {
        	return DOMUtil.getElementValue((Element) form_id);
        }
        return null;
	}
	
	/**
	 * Save submitted form's instance
	 * 
	 * @param formId
	 * @param is instance input stream to save
	 * @throws IOException 
	 */
	protected void saveSubmittedData(String formId, InputStream is) throws IOException {
		
		String path = 
			new StringBuilder(SUBMITTED_DATA_PATH)
			.append(slash)
			.append(formId)
			.append(slash)
			.toString()
		;
			
		String fileName = System.currentTimeMillis() + ".xml";
		
		//logger.info("Saving submitted instance to webdav path: " + path + fileName);

		IWSlideService service = getIWSlideService();
		service.uploadFileAndCreateFoldersFromStringAsRoot(path, fileName, is, "text/xml", false);
	}
	
	protected IWSlideService getIWSlideService() throws IBOLookupException {
		
		try {
			return (IWSlideService) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), IWSlideService.class);
		} catch (IBOLookupException e) {
			//logger.log(Level.SEVERE, "Error getting IWSlideService");
			throw e;
		}
	}
}