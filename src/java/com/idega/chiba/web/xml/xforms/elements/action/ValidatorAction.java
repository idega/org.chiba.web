package com.idega.chiba.web.xml.xforms.elements.action;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.chiba.xml.xforms.Container;
import org.chiba.xml.xforms.XFormsElement;
import org.chiba.xml.xforms.action.AbstractBoundAction;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.core.ModelItem;
import org.chiba.xml.xforms.exception.XFormsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.idega.chiba.web.xml.xforms.elements.ErrorMessageHandler;
import com.idega.chiba.web.xml.xforms.elements.ErrorMessageHandler.ErrorType;
import com.idega.chiba.web.xml.xforms.util.XFormsUtil;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.util.CoreConstants;
import com.idega.util.expression.ELUtil;
import com.idega.util.xml.NamespaceContextImpl;
import com.idega.util.xml.XPathUtil;

/**
 * TODO: send events only for constraints, that exist (if it has constraint, or has validation rule etc)
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.5 $
 *
 * Last modified: $Date: 2008/10/02 13:42:05 $ by $Author: civilis $
 *
 */
public class ValidatorAction extends AbstractBoundAction {

	@Autowired
	private ErrorMessageHandler errorMessageHandler;
	
	private String validateIf;
	private Locale formLocale;
	private String setErrorId;
	private String submissionExp;
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
        
        String setErrorId = getXFormsAttribute("seterror");
        
        if(setErrorId == null || setErrorId.length() == 0)
        	setErrorId = "formSetErrorHandler";
        
        setSetErrorId(setErrorId);
        
    	String submissionExp = getXFormsAttribute("submission");
    	
    	setSubmissionExp(submissionExp);
    	
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
    	
    	String errMsg = null;
    	
    	boolean doRequiredValidation = false;
    	
    	String submissionExp = getSubmissionExp();
    	
    	Model submissionModel = getContainerObject().getModel("submission_model");
    	
    	String instanceId;
    	
    	if(submissionExp == null || submissionExp.length() == 0) {
    		
    		instanceId = "control-instance";
    		submissionExp = "instance('control-instance')/submission";
    		
    	} else {
    		instanceId = submissionModel.computeInstanceId(submissionExp);
    	}
    	
    	Instance controlInstance = submissionModel.getInstance(instanceId);
		String submissionPhase = controlInstance.getNodeValue(submissionExp);
		
		doRequiredValidation = "true".equals(submissionPhase);
    	
		if(doRequiredValidation && modelItem.isRequired()) {
			
//			validating required only after submit button was pressed
        	
        	String val = modelItem.getValue();
        	ErrorType errType = ErrorType.required;
        	
        	if(val == null || val.length() == 0) {
        		
        		errMsg = getErrorMessage(errType);
        	}
        }
        
		if(errMsg == null) {
			
//			doing standard validation - datatype and constraint
		
			getModel().getValidator().validate(modelItem);
        	
        	if(!modelItem.getLocalUpdateView().isDatatypeValid()) {
        		
        		errMsg = getErrorMessage(ErrorType.validation);
        		
        	} else if(!modelItem.getLocalUpdateView().isConstraintValid()) {
        		
        		errMsg = getErrorMessage(ErrorType.constraint);
        	}
		}
    	
    	if(errMsg == null && validateIf != null && validateIf.length() != 0) {

//    		doing custom validation
    		boolean validates = evalCondition(getElement(), validateIf);
    		
    		if(!validates) {
    			errMsg = getErrorMessage(ErrorType.custom);
    		}
    	}
    	
    	if(errMsg != null) {
    		
    		modelItem.getLocalUpdateView().setDatatypeValid(false);
    	} else {
    		modelItem.getLocalUpdateView().setDatatypeValid(true);
    	}
    	
//    	sending error msg, or empty, if everything is valid
    	getErrorMessageHandler().send(modelItem, container, getSetErrorId(), componentId, errMsg != null ? errMsg : CoreConstants.EMPTY);
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
        		Object val = XFormsUtil.getValueFromExpression(messageValuesByType.get(errType), this);
        		
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

	String getSetErrorId() {
		return setErrorId;
	}

	void setSetErrorId(String setErrorId) {
		this.setErrorId = setErrorId;
	}

	String getSubmissionExp() {
		return submissionExp;
	}

	void setSubmissionExp(String submissionExp) {
		this.submissionExp = submissionExp;
	}
}