package com.idega.chiba.web.xml.xforms.connector.process;

import java.net.URI;
import java.util.Map;

import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.xforms.connector.AbstractConnector;
import org.chiba.xml.xforms.connector.SubmissionHandler;
import org.chiba.xml.xforms.core.Submission;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.idega.jbpm.exe.VariablesHandler;
import com.idega.webface.WFUtil;

/**
 * TODO: write javadoc
 * 
 * @author Vytautas Čivilis
 * @version $Id: ProcessSubmissionHandler.java,v 1.2 2007/10/01 16:31:00 civilis Exp $
 */
public class ProcessSubmissionHandler extends AbstractConnector implements SubmissionHandler {
    
    /**
     * TODO: write javadoc
     */
	@SuppressWarnings("unchecked")
    public Map submit(Submission submission, Node instance) throws XFormsException {

    	System.out.println("proc submit");
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
    	
//    	TODO: do this somewhere else and in correct way
    	String action = submission.getElement().getAttribute("action");
    	String taskId = action.substring(action.indexOf("taskId=")+"taskId=".length(), action.length());
    	
    	vh.submit(Long.parseLong(taskId), instance);
    	
    	return null;
    }
}