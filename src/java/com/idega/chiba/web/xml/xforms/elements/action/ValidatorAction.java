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
import com.idega.chiba.web.xml.xforms.util.XFormsUtil;
import com.idega.chiba.web.xml.xforms.validation.ErrorType;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.xml.NamespaceContextImpl;
import com.idega.util.xml.XPathUtil;

/**
 * TODO: send events only for constraints, that exist (if it has constraint, or has validation rule etc)
 *
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.10 $
 *
 * Last modified: $Date: 2008/11/05 09:02:31 $ by $Author: civilis $
 *
 */
public class ValidatorAction extends AbstractBoundAction {

	private static final Logger LOGGER = Logger.getLogger(ValidatorAction.class.getName());

	@Autowired
	private ErrorMessageHandler errorMessageHandler;

	private Locale formLocale;

	private String	validateIf,
					errorIf,
					setErrorId,
					submissionExp,
					componentId;

	private Map<ErrorType, String> messageValuesByType;

	private static XPathUtil messageXPUT;

	static {
		NamespaceContextImpl nmspcContext = new NamespaceContextImpl();
        nmspcContext.addPrefix("idega", "http://idega.com/xforms");
        messageXPUT = new XPathUtil(".//idega:message", nmspcContext);
	}

	public static final String	VALIDATEIF_ATT = "validateif",
								ERROR_IF_ATT = "errorif",
								ERRORTYPE_ATT = "errorType",
								VALUE_ATT = "value",
								COMPONENT_ID_ATT = "componentId";

    public ValidatorAction(Element element, Model model) {
        super(element, model);
    }

    @Override
	public void init() throws XFormsException {
        super.init();

        String setErrorId = getXFormsAttribute("seterror");
        if (StringUtil.isEmpty(setErrorId))
        	setErrorId = "formSetErrorHandler";
        setSetErrorId(setErrorId);

    	String submissionExp = getXFormsAttribute("submission");
    	setSubmissionExp(submissionExp);

        String validateIf = getXFormsAttribute(VALIDATEIF_ATT);
        setValidateIf(validateIf);

        String errorIf = getXFormsAttribute(ERROR_IF_ATT);
        setErrorIf(errorIf);

        String componentId = getXFormsAttribute(COMPONENT_ID_ATT);
        if (componentId == null) {
        	XFormsElement parent = getParentObject();
        	componentId = parent.getId();
		}

        if (!componentId.startsWith(XFormsUtil.CTID)) {
        	String xformId = XFormsUtil.getFormId(getContainerObject().getDocument());
        	LOGGER.warning("Component ID is _probably_ not correct. The component id resolved = "+componentId +" xform id = "+ xformId);
        }

        setComponentId(componentId);

        NodeList messages = messageXPUT.getNodeset(getElement());
        if(messages != null && messages.getLength() != 0) {
        	messageValuesByType = new HashMap<ErrorType, String>(messages.getLength());
        	for (int i = 0; i < messages.getLength(); i++) {
        		Element msgEle = (Element)messages.item(i);

        		String messageType = msgEle.getAttribute(ERRORTYPE_ATT);
        		String messageValue = msgEle.getAttribute(VALUE_ATT);

        		final ErrorType errType = ErrorType.getByStringRepresentation(messageType);
        		messageValuesByType.put(errType, messageValue);
			}
        }
    }

    @Override
	public void perform() throws XFormsException {
        super.perform();

        Container container = getContainerObject();

        Instance instance = this.model.getInstance(getInstanceId());
        String pathExpression = getLocationPath();
        ModelItem modelItem = instance.getModelItem(pathExpression);

        String componentId = getComponentId();

    	String validateIf = getValidateIf();
    	String errorIf = getErrorIf();

    	String errMsg = null;

    	boolean doRequiredValidation = false;

    	String submissionExp = getSubmissionExp();

    	Model submissionModel = getContainerObject().getModel("submission_model");

    	String instanceId;

    	if (StringUtil.isEmpty(submissionExp)) {
    		instanceId = "control-instance";
    		submissionExp = "instance('control-instance')/submission";
    	} else {
    		instanceId = submissionModel.computeInstanceId(submissionExp);
    	}

    	if (modelItem == null) {
        	LOGGER.warning("ModelItem is undefined at the path: " + pathExpression + ", instance: " + instanceId);
        }

    	Instance controlInstance = submissionModel.getInstance(instanceId);
		String submissionPhase = controlInstance.getNodeValue(submissionExp);

		doRequiredValidation = Boolean.TRUE.toString().equals(submissionPhase);

		if (doRequiredValidation) {
			if (modelItem == null) {
				errMsg = getErrorMessage(ErrorType.required);
			} else if (modelItem.isRequired()) {
				//	Validating required only after submit button was pressed
	        	String val = modelItem.getValue();
	        	ErrorType errType = ErrorType.required;
	        	if (StringUtil.isEmpty(val)) {
	        		errMsg = getErrorMessage(errType);
	        	}
	        }
		}

		if (errMsg == null) {
			if (modelItem == null) {
				errMsg = getErrorMessage(ErrorType.validation);
			} else {
				//	Doing standard validation - data type and constraint
				getModel().getValidator().validate(modelItem);
	        	if (!modelItem.getLocalUpdateView().isDatatypeValid()) {
	        		errMsg = getErrorMessage(ErrorType.validation);
	        	} else if (!modelItem.getLocalUpdateView().isConstraintValid()) {
	        		errMsg = getErrorMessage(ErrorType.constraint);
	        	}
			}
		}

    	if (errMsg == null) {
    		boolean error = false;
    		if (!StringUtil.isEmpty(validateIf)) {
    			boolean validates = evalCondition(getElement(), validateIf);

    			if (StringUtil.isEmpty(errorIf)) {
    				error = !validates;								//	Checking "validateif" condition
    			} else if (validates) {
    				error = evalCondition(getElement(), errorIf);	//	Checking "errorif" only if "validateif" is true
    			} else {
    				error = Boolean.FALSE;							//	"validateif" is false, "errorif" is provided, but not checking it. Default value - no error.
    			}
    		} else if (!StringUtil.isEmpty(errorIf)) {
    			error = evalCondition(getElement(), errorIf);		//	"validateif" is not provided, checking only "errorif"
    		}

    		errMsg = error ? getErrorMessage(ErrorType.custom) : null;
    	}

    	if (modelItem == null) {
    		LOGGER.warning("The error '" + errMsg + "' should be sent to the model item at " + pathExpression + ", instance: " + instanceId);
    	} else {
	    	modelItem.getLocalUpdateView().setDatatypeValid(errMsg == null);

	    	//	Sending error message, or empty, if everything is valid
	    	getErrorMessageHandler().send(modelItem, container, getSetErrorId(), componentId, errMsg == null ? CoreConstants.EMPTY : errMsg);
    	}

    	if (errMsg != null) {
    		getEvent().preventDefault();
    		getEvent().stopPropagation();
    	}
    }

    protected Locale getFormLocale() {
    	if (formLocale == null) {
        	try {
        		Model dataModel = getContainerObject().getModel("data_model");
            	Instance instance = dataModel.getInstance("localized_strings");
        		String localeStr = instance.getNodeValue("instance('localized_strings')/current_language");
        		if (StringUtil.isEmpty(localeStr)) {
        			localeStr = "is_IS";
        		}
       			formLocale = ICLocaleBusiness.getLocaleFromLocaleString(localeStr);
    		} catch (XFormsException e) {
    			formLocale = new Locale("is", "IS");
    			LOGGER.log(Level.WARNING, "Exception while resolving current form language, using default = "+formLocale.toString(), e);
    		}
    	}
    	return formLocale;
	}

    protected String getErrorMessage(ErrorType errType) {
    	String message = null;
    	if (messageValuesByType != null && messageValuesByType.containsKey(errType)) {
    		try {
        		Object val = XFormsUtil.getValueFromExpression(messageValuesByType.get(errType), this);
        		if (val != null)
        			message = val.toString();
    		} catch (XFormsException e) {
    			LOGGER.log(Level.WARNING, "Exception while resolving message from message value expression = "+messageValuesByType.get(errType), e);
    		}
    	}

    	if (message == null) {
    		message = errType.getDefaultErrorMessage(getFormLocale());
    		LOGGER.warning("Component with ID '" + this.componentId + "' does not have error message for error type '" + errType +
    				"', using default message: " + message + ". Validator: " + this);
    	}

    	return message;
	}

    public ErrorMessageHandler getErrorMessageHandler() {
    	if (errorMessageHandler == null)
    		ELUtil.getInstance().autowire(this);

    	return errorMessageHandler;
    }

	public void setErrorMessageHandler(ErrorMessageHandler errorMessageHandler) {
		this.errorMessageHandler = errorMessageHandler;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
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

	public String getErrorIf() {
		return errorIf;
	}

	public void setErrorIf(String errorIf) {
		this.errorIf = errorIf;
	}
}