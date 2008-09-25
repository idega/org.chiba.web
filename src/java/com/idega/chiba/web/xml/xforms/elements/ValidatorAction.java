package com.idega.chiba.web.xml.xforms.elements;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.jxpath.JXPathContext;
import org.chiba.xml.xforms.Container;
import org.chiba.xml.xforms.XFormsElement;
import org.chiba.xml.xforms.action.AbstractBoundAction;
import org.chiba.xml.xforms.core.BindingResolver;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.core.ModelItem;
import org.chiba.xml.xforms.exception.XFormsComputeException;
import org.chiba.xml.xforms.exception.XFormsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.idega.chiba.web.xml.xforms.elements.ErrorMessageHandler.ErrorType;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.util.CoreConstants;
import com.idega.util.expression.ELUtil;
import com.idega.util.xml.NamespaceContextImpl;
import com.idega.util.xml.XPathUtil;

/**
 * TODO: send events only for constraints, that exist (if it has constraint, or has validation rule etc)
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.7 $
 *
 * Last modified: $Date: 2008/09/25 16:12:48 $ by $Author: civilis $
 *
 */
public class ValidatorAction extends AbstractBoundAction {

	@Autowired
	private ErrorMessageHandler errorMessageHandler;
	
	private String validateIf;
	private Locale formLocale;
	private Map<ErrorType, String> messageValuesByType;
	
	private static XPathUtil messageXPUT;
	
	static {
		
		NamespaceContextImpl nmspcContext = new NamespaceContextImpl();
        nmspcContext.addPrefix("idega", "http://idega.com/xforms");
        messageXPUT = new XPathUtil(".//idega:message", nmspcContext);
	}
	
	public static final String VALIDATEIF_ATT = "validateif";
	public static final String ERRORTYPE_ATT = "errorType";
	public static final String VALUE_ATT = "value";
	
    public ValidatorAction(Element element, Model model) {
        super(element, model);
    }

    public void init() throws XFormsException {
        super.init();
        
        String validateIf = getXFormsAttribute(VALIDATEIF_ATT);
        setValidateIf(validateIf);
        
        NodeList messages = messageXPUT.getNodeset(getElement());
        
        if(messages != null && messages.getLength() != 0) {
        	
        	messageValuesByType = new HashMap<ErrorType, String>(messages.getLength());
        
        	for (int i = 0; i < messages.getLength(); i++) {
			
        		Element msgEle = (Element)messages.item(i);
        		
        		String messageType = msgEle.getAttribute(ERRORTYPE_ATT);
        		String messageValue = msgEle.getAttribute(VALUE_ATT);
        		
        		final ErrorType errType;
        		
        		if(messageType == null || messageType.length() == 0) {
        			errType = ErrorType.custom;
        		} else {
        			errType = ErrorType.getByStringRepresentation(messageType);
        		}
        		
        		messageValuesByType.put(errType, messageValue);
			}
        }
    }

    public void perform() throws XFormsException {
        super.perform();
        
        Container container = getContainerObject();
        
        Instance instance = this.model.getInstance(getInstanceId());
        String pathExpression = getLocationPath();
        ModelItem modelItem = instance.getModelItem(pathExpression);
        
        XFormsElement parent = getParentObject();
    	String componentId = parent.getId();
    	
    	String validateIf = getValidateIf();
    	
    	if(validateIf == null || validateIf.length() == 0) {
    		
//    		doing standard validation
    		
    		if(modelItem.isRequired()) {
            	
            	String val = modelItem.getValue();
            	ErrorType errType = ErrorType.required;
            	
            	if(val == null || val.length() == 0) {
            		
            		String message = getErrorMessage(errType);
            		getErrorMessageHandler().send(modelItem, container, this.target, componentId, message, errType);
            	} else {
            		getErrorMessageHandler().send(modelItem, container, this.target, componentId, CoreConstants.EMPTY, errType);
            	}
            }
            
    		getModel().getValidator().validate(modelItem);
        	
        	if(!modelItem.getLocalUpdateView().isDatatypeValid()) {
        		
        		String message = getErrorMessage(ErrorType.validation);
        		getErrorMessageHandler().send(modelItem, container, this.target, componentId, message, ErrorType.validation);
        		
        	} else {
        		getErrorMessageHandler().send(modelItem, container, this.target, componentId, CoreConstants.EMPTY, ErrorType.validation);
        	}
        	
        	if(!modelItem.getLocalUpdateView().isConstraintValid()) {
        		
        		String message = getErrorMessage(ErrorType.constraint);
        		getErrorMessageHandler().send(modelItem, container, this.target, componentId, message, ErrorType.constraint);
        		
        	} else {
        		getErrorMessageHandler().send(modelItem, container, this.target, componentId, CoreConstants.EMPTY, ErrorType.constraint);
        	}
    		
    	} else {
    		
//    		doing custom validation
    		boolean validates = evalCondition(getElement(), validateIf);
    		
    		if(validates) {
    			
//    			TODO: support more than one custom validation, that means, we need to somehow identify each custom validator
    			getErrorMessageHandler().send(modelItem, container, this.target, componentId, CoreConstants.EMPTY, ErrorType.custom);
    			
    		} else {
    			
    			String message = getErrorMessage(ErrorType.custom);
    			getErrorMessageHandler().send(modelItem, container, this.target, componentId, message, ErrorType.custom);
    		}
    	}
    }
    
    protected Locale getFormLocale() {
		
    	if(formLocale == null) {
    	
        	try {
        		Model dataModel = getContainerObject().getModel("data_model");
            	Instance instance = dataModel.getInstance("localized_strings");
        		String localeStr = instance.getNodeValue("instance('localized_strings')/current_language");
        		
        		if(localeStr != null && localeStr.length() != 0) {
        			
        			formLocale = ICLocaleBusiness.getLocaleFromLocaleString(localeStr);
        			
        		} else {
        		
        			formLocale = new Locale("is", "IS");
        		}
    			
    		} catch (XFormsException e) {

    			formLocale = new Locale("is", "IS");
    			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Exception while resolving current form language, using default = "+formLocale.toString(), e);
    		}
    	}
    	
    	return formLocale;
	}
    
    protected String getErrorMessage(ErrorType errType) {
    	
    	String message = null;
		
    	if(messageValuesByType != null && messageValuesByType.containsKey(errType)) {
    	
    		try {
        		Object val = computeValueAttribute(messageValuesByType.get(errType));
        		
        		if(val != null)
        			message = val.toString();
        		
    		} catch (XFormsException e) {
    			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Exception while resolving message from message value expression = "+messageValuesByType.get(errType), e);
    		}
    	}
    	
    	if(message == null)
    		message = errType.getDefaultErrorMessage(getFormLocale());
    	
    	return message;
	}
    
    /**
     * copied from Output, whereas it was copied from SetValueAction
     * @param valueAttribute
     * @return
     * @throws XFormsException
     */
    protected Object computeValueAttribute(String valueAttribute) throws XFormsException {
        String pathExpression = BindingResolver.getExpressionPath(this, this.repeatItemId);
        Instance instance = this.model.getInstance(this.model.computeInstanceId(pathExpression));
        if (!instance.existsNode(pathExpression)) {
            return null;
        }

        // todo: implement XPathProcessor abstraction, this code is copied from SetValueAction
        // since jxpath doesn't provide a means for evaluating an expression
        // in a certain context, we use a trick here: the expression will be
        // evaluated during getPointer and the result stored as a variable
        JXPathContext context = instance.getInstanceContext();

        String currentPath = getParentContextPath(this.element);
        context.getVariables().declareVariable("currentContextPath", currentPath);
        context.getVariables().declareVariable("contextmodel", getModelId());
        try {
            context.getPointer(pathExpression + "[chiba:declare('output-value', " + valueAttribute + ")]");
        }
        catch (Exception e) {
            throw new XFormsComputeException("invalid value expression at " + this, e, this.target, valueAttribute);
        }
        Object value = context.getValue("chiba:undeclare('output-value')");
        context.getVariables().undeclareVariable("currentContextPath");
        context.getVariables().undeclareVariable("contextmodel");

        // check for string conversion to prevent sth. like "5 + 0" to be evaluated to "5.0"
        if (value instanceof Double) {
            // additionaly check for special cases
            double doubleValue = ((Double) value).doubleValue();
            if (!(Double.isNaN(doubleValue) || Double.isInfinite(doubleValue))) {
                value = context.getValue("string(" + value + ")");
            }
        }

        return value;
    }
    
    public ErrorMessageHandler getErrorMessageHandler() {

    	if(errorMessageHandler == null)
    		ELUtil.getInstance().autowire(this);
    	
    	return errorMessageHandler;
    }

	public void setErrorMessageHandler(ErrorMessageHandler errorMessageHandler) {
		this.errorMessageHandler = errorMessageHandler;
	}

	public String getValidateIf() {
		return validateIf;
	}

	public void setValidateIf(String validateIf) {
		this.validateIf = validateIf;
	}
}