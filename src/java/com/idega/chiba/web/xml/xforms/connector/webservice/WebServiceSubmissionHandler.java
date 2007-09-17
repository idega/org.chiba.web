package com.idega.chiba.web.xml.xforms.connector.webservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.xforms.connector.AbstractConnector;
import org.chiba.xml.xforms.connector.SubmissionHandler;
import org.chiba.xml.xforms.core.Submission;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Node;

import com.idega.cxf.test.service.HelloSpring;
import com.idega.webface.WFUtil;

/**
 * The file submission driver serializes and submits instance data to a file.
 * <p/>
 * When using the <code>put</code> submission method, the driver only supports
 * the replace mode <code>none</code>. It simply serializes the instance data to
 * the file denoted by the connector URI. When this file exists, it will be
 * overwritten silently, otherwise it will be created.
 * <p/>
 * 
 * @author Vytautas Čivilis
 * @version $Id: WebServiceSubmissionHandler.java,v 1.1 2007/09/17 13:32:43 civilis Exp $
 */
public class WebServiceSubmissionHandler extends AbstractConnector implements SubmissionHandler {
    
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

    	if (!submission.getReplace().equalsIgnoreCase("none"))
            throw new XFormsException("Submission mode '" + submission.getReplace() + "' not supported");
    	
    	if(!submission.getMethod().equalsIgnoreCase("put") && !submission.getMethod().equalsIgnoreCase("post"))
    		throw new XFormsException("Submission method '" + submission.getMethod() + "' not supported");
    	
    	if(submission.getMethod().equalsIgnoreCase("put")) {
    		//update (put)
    		//currently unsupported
    		throw new XFormsException("Submission method '" + submission.getMethod() + "' not supported");
    		
    	} else {
    		//insert (post)
    	}
    	
    	HelloSpring wsClient = (HelloSpring)WFUtil.getBeanInstance("helloSpringClient");
    	System.out.println("h: "+wsClient.sayHi("labadiena"));
    	
    	
    	try {
    		
    		System.out.println("sending..........");
    		DOMUtil.prettyPrintDOM(instance);
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		serialize(submission, instance, out);
        	System.out.println(wsClient.sendNode(out.toByteArray()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
//    	System.out.println("submitting through ws...........");
//    	System.out.println("action:"+submission.getAction());
//    	System.out.println("id:"+submission.getId());
//    	System.out.println("instance:"+submission.getInstance());
//    	DOMUtil.prettyPrintDOM(instance);
    	
    	return null;
    }
}