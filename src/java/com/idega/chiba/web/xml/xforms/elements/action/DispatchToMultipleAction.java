package com.idega.chiba.web.xml.xforms.elements.action;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.chiba.xml.xforms.Container;
import org.chiba.xml.xforms.action.AbstractAction;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsBindingException;
import org.chiba.xml.xforms.exception.XFormsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;

import com.idega.chiba.ChibaUtils;
import com.idega.chiba.facade.IdegaFluxFacade;
import com.idega.chiba.web.xml.xforms.validation.XFormSubmissionValidator;
import com.idega.event.ScriptCallerInterface;
import com.idega.util.CoreUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.xml.XPathUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/10/16 19:17:55 $ by $Author: civilis $
 *
 */
public class DispatchToMultipleAction extends AbstractAction {
    
	private static final Logger LOGGER = Logger.getLogger(DispatchToMultipleAction.class.getName());
	
    private String	nameAttribute,
    				targetAttribute;
    
    private boolean bubbles = false,
    				cancelable = true,
    				stopOnError = false;

    public DispatchToMultipleAction(Element element, Model model) {
        super(element, model);
    }
    
    @Override
	public void init() throws XFormsException {
        super.init();

        this.nameAttribute = getXFormsAttribute(NAME_ATTRIBUTE);
        if (this.nameAttribute == null) {
            throw new XFormsBindingException("missing name attribute at " + this, this.target, null);
        }

        this.targetAttribute = getXFormsAttribute(TARGET_ATTRIBUTE);
        if (this.targetAttribute == null) {
            throw new XFormsBindingException("missing target attribute at " + this, this.target, null);
        }

        String bubblesAttribute = getXFormsAttribute(BUBBLES_ATTRIBUTE);
        if (bubblesAttribute != null) {
            this.bubbles = Boolean.valueOf(bubblesAttribute).booleanValue();
        }

        String cancelableAttribute = getXFormsAttribute(CANCELABLE_ATTRIBUTE);
        if (cancelableAttribute != null) {
            this.cancelable = Boolean.valueOf(cancelableAttribute).booleanValue();
        }
        
        String stopOnErrorAttribute = getXFormsAttribute("stopOnError");
        
        if (stopOnErrorAttribute != null && "true".equals(stopOnErrorAttribute)) {
        	this.stopOnError = true;
        }
    }

    private IdegaFluxFacade getFlux() {
    	try {
	    	IdegaFluxFacade flux = ELUtil.getInstance().getBean(IdegaFluxFacade.BEAN_NAME);
	    	return flux;
    	} catch (Exception e) {}
    	return null;
    }
    
    @Autowired
    private XFormSubmissionValidator submissionValidator;

    private XFormSubmissionValidator getXFormSubmissionValidator() {
    	if (submissionValidator == null)
    		ELUtil.getInstance().autowire(this);
    	return submissionValidator;
    }
    
    public void perform() throws XFormsException {
    	if (targetAttribute.startsWith("//h:body//*[starts-with(@id, 'fbc_')]")) {
    		//	Submit action!
    		IdegaFluxFacade flux = getFlux();
    		if (flux != null) {
    			String sessionKey = flux.getCurrentXFormSessionKey();
    			String uri = ChibaUtils.getInstance().getXFormActionUri(sessionKey);
    			if (!getXFormSubmissionValidator().isPossibleToSubmitXForm(uri)) {
    				ScriptCallerInterface scriptCaller = ELUtil.getInstance().getBean(ScriptCallerInterface.BEAN_NAME);
    				if (scriptCaller != null) {
    					scriptCaller.executeScript(CoreUtil.getIWContext().getSession().getId(),
    							"if (typeof XFormSessionHelper != 'undefined') {XFormSessionHelper.navigateFromXForm();}");
    				}
    				
    				flux.setError(sessionKey + uri);
    				throw new XFormsException("XForm at '" + uri + "' by session ID '" + sessionKey + "' can not be submitted - user must be logged in!");
    			}
    		}
    	}
    	
    	Document xform = getContainerObject().getDocument();
    	
    	XPathUtil ut = new XPathUtil(targetAttribute);
    	
    	NodeList components = ut.getNodeset(xform);
    	
    	if (components != null) {
    		Container container = getContainerObject();
    		for (int i = 0; i < components.getLength(); i++) {
    			Node node = components.item(i);
    			if (node instanceof EventTarget) {
	    			EventTarget targ = (EventTarget) node;
	    			boolean cancelled = false;
	    			try {
	    				cancelled = container.dispatch(targ, nameAttribute, null, bubbles, cancelable);
	    			} catch (Exception e) {
	    				LOGGER.log(Level.WARNING, "Error dispatching action to " + targ + ", name: " + nameAttribute + ", bubbles: " + bubbles + ", cancelable: " + cancelable,
	    						e);
	    				throw new XFormsException(e);
	    			}
	    			
	    			if (isStopOnError() && cancelled)
	    				break;
    			} else {
    				LOGGER.warning("Node " + node + " is not type of " + EventTarget.class + ", unable to dispatch action. Name: " + nameAttribute + ", bubbles: " + bubbles +
    						", cancelable: " + cancelable);
    			}
			}
    	}
    }

	boolean isStopOnError() {
		return stopOnError;
	}
}