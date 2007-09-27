package com.idega.chiba.web.xml.xforms.connector.process;

import java.util.Map;

import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.xforms.connector.AbstractConnector;
import org.chiba.xml.xforms.connector.SubmissionHandler;
import org.chiba.xml.xforms.core.Submission;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Node;

import com.idega.jbpm.exe.VariablesHandler;
import com.idega.webface.WFUtil;

/**
 * TODO: write javadoc
 * 
 * @author Vytautas Čivilis
 * @version $Id: ProcessSubmissionHandler.java,v 1.1 2007/09/27 16:26:01 civilis Exp $
 */
public class ProcessSubmissionHandler extends AbstractConnector implements SubmissionHandler {
    
    /**
     * TODO: write javadoc
     */
	@SuppressWarnings("unchecked")
    public Map submit(Submission submission, Node instance) throws XFormsException {

    	System.out.println("wssubmit");
    	//method - post, replace - none
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
    	VariablesHandler vh = (VariablesHandler)WFUtil.getBeanInstance("process_xforms_variablesHandler");
    	
    	System.out.println("submission element: ");
    	//submission.get
    	DOMUtil.prettyPrintDOM(submission.getElement());
    	
    	System.out.println("vh got: "+vh);
    	System.out.println("instance: ");
    	DOMUtil.prettyPrintDOM(instance);
    	
    	vh.submit(13, instance);
    	
    	return null;
    }
}