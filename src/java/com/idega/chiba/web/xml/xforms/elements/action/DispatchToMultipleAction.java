package com.idega.chiba.web.xml.xforms.elements.action;

import org.chiba.xml.xforms.Container;
import org.chiba.xml.xforms.action.AbstractAction;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsBindingException;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;

import com.idega.util.xml.XPathUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/09/26 10:26:51 $ by $Author: civilis $
 *
 */
public class DispatchToMultipleAction extends AbstractAction {
    
    private String nameAttribute;
    private String targetAttribute;
    private boolean bubbles = false;
    private boolean cancelable = true;

    public DispatchToMultipleAction(Element element, Model model) {
        super(element, model);
    }
    
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
    }

    public void perform() throws XFormsException {
    	
    	Document xform = getContainerObject().getDocument();
    	
//    	xpath = "//h:body//*[starts-with(@id, 'fbc_')]";
//    	
    	XPathUtil ut = new XPathUtil(targetAttribute);
    	
    	NodeList components = ut.getNodeset(xform);
    	
    	if(components != null) {
    		
    		Container container = getContainerObject();
    		
    		for (int i = 0; i < components.getLength(); i++) {
				
    			EventTarget targ = (EventTarget)components.item(i);
    			container.dispatch(targ, nameAttribute, null, bubbles, cancelable);
			}
    	}
    }
}