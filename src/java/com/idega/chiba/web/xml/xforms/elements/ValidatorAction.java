package com.idega.chiba.web.xml.xforms.elements;

import java.util.Locale;

import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.xforms.Container;
import org.chiba.xml.xforms.XFormsElement;
import org.chiba.xml.xforms.action.AbstractBoundAction;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.core.ModelItem;
import org.chiba.xml.xforms.exception.XFormsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.idega.chiba.web.xml.xforms.elements.ErrorMessageHandler.ErrorType;
import com.idega.util.expression.ELUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.3 $
 *
 * Last modified: $Date: 2008/09/24 13:46:18 $ by $Author: civilis $
 *
 */
public class ValidatorAction extends AbstractBoundAction {

	@Autowired
	private ErrorMessageHandler errorMessageHandler;
	
    /**
     * Creates a message action implementation.
     *
     * @param element the element.
     * @param model the context model.
     */
    public ValidatorAction(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     */
    public void init() throws XFormsException {
        super.init();
    }

    // implementation of 'org.chiba.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>message</code> action.
     *
     * @throws XFormsException if an error occurred during <code>message</code>
     * processing.
     */
    public void perform() throws XFormsException {
        super.perform();
        
        System.out.println("in action, get id="+getId());

//      XFormsElement parent = getParentObject();
        
        Container container = getContainerObject();
        
        Instance instance = this.model.getInstance(getInstanceId());
        String pathExpression = getLocationPath();
        ModelItem modelItem = instance.getModelItem(pathExpression);
        
        XFormsElement parent = getParentObject();
        
        boolean errorSent = false;
        
        if(modelItem.isRequired()) {
        	
        	System.out.println("mi id="+modelItem.getId());
        	System.out.println("id from mi element="+((Element)modelItem.getNode()).getAttribute("id"));
        	System.out.println("parent id = "+parent.getId());
        	
        	String val = modelItem.getValue();
        	
        	if(val == null || val.length() == 0) {
        		
        		System.out.println("REQUIRED ERROR, post msg");
        		
//        		ModelItem mi, Container container, EventTarget target, String message
        		errorSent = true;
        		
        		Locale locale = new Locale("en");
        		String message = getErrorMessageHandler().getDefaultErrorMessage(locale, ErrorType.required);
        		String componentId = parent.getId();
        		
        		getErrorMessageHandler().send(modelItem, container, this.target, componentId, message);
        	}
        }
        
        if(!errorSent) {
        
        	getModel().getValidator().validate(modelItem);
        	
        	System.out.println("is datatype valid= "+modelItem.getLocalUpdateView().isDatatypeValid());
            System.out.println("is constraint valid= "+modelItem.getLocalUpdateView().isConstraintValid());
            
            if(!modelItem.getLocalUpdateView().isDatatypeValid() || !modelItem.getLocalUpdateView().isConstraintValid()) {
            	
            	Locale locale = new Locale("en");
        		String message = getErrorMessageHandler().getDefaultErrorMessage(locale, 
        				!modelItem.getLocalUpdateView().isDatatypeValid() ? ErrorType.validation : ErrorType.constraint);
        		String componentId = parent.getId();
        		
        		getErrorMessageHandler().send(modelItem, container, this.target, componentId, message);
            }
        }
    }
    
    public ErrorMessageHandler getErrorMessageHandler() {

    	if(errorMessageHandler == null)
    		ELUtil.getInstance().autowire(this);
    	
    	return errorMessageHandler;
    }

	public void setErrorMessageHandler(ErrorMessageHandler errorMessageHandler) {
		this.errorMessageHandler = errorMessageHandler;
	}
}