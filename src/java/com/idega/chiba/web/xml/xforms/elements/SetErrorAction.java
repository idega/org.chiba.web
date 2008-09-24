package com.idega.chiba.web.xml.xforms.elements;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chiba.xml.xforms.action.AbstractBoundAction;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;

import com.idega.util.xml.XPathUtil;

public class SetErrorAction extends AbstractBoundAction{
	
	protected static Log LOGGER = LogFactory.getLog(SetErrorAction.class);

	public SetErrorAction(Element element, Model model) {
		super(element, model);
		// TODO Auto-generated constructor stub
	}
	
    public void init() throws XFormsException {
        super.init();
       
    }        

	@SuppressWarnings("unchecked")
	public void perform() throws XFormsException {
	    super.perform();
	    
	    System.out.println("context:");
	    System.out.println(eventContextInfo);
	    
	    Instance instance = this.model.getInstance(getInstanceId());
	    
        String pathExpression = getLocationPath();
        
        if (!instance.existsNode(pathExpression)) {
            getLogger().warn(this + " perform: nodeset '" + pathExpression + "' is empty");
            return;
        }
        
		int contextSize = instance.countNodeset(pathExpression);
		String origin, after;
		
	    
	    if (eventContextInfo instanceof Map) { 
	    	
			Map<String, Object> errorMsgs = (Map<String, Object>) eventContextInfo;
			
			String errorMsg = errorMsgs.get(ErrorMessageHandler.messageContextAtt).toString();
			String targetAtt = errorMsgs.get(ErrorMessageHandler.targetContextAtt).toString();
				  
			System.out.println("messages : "+ errorMsg + "  " + "id " + targetAtt);	
				
			origin = new StringBuffer(pathExpression).append('[').append(contextSize).append(']').toString();
				  
			if (instance.getNodeValue(origin).equals("")) {
				
				instance.setNodeValue(pathExpression, errorMsg != null ? errorMsg : "");
						
			} else {

					contextSize++;
						
					after = new StringBuffer(pathExpression).append('[').append(contextSize).append(']').toString();

					instance.insertNode(origin, after);
					
					instance.setNodeValue(after, errorMsg != null ? errorMsg : "");
			}
				
			
	    }
            
	    doRecalculate(true);
        doRevalidate(true);
        doRefresh(true);

    }
		
	

}
