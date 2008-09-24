package com.idega.chiba.web.xml.xforms.elements;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.xforms.action.AbstractBoundAction;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsBindingException;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SetErrorAction extends AbstractBoundAction{
	
	protected static Log LOGGER = LogFactory.getLog(SetErrorAction.class);
	private String nodeValue;
	private String valueAttribute;
	private String targetAttribute;

	public SetErrorAction(Element element, Model model) {
		super(element, model);
		// TODO Auto-generated constructor stub
	}
	
    public void init() throws XFormsException {
        super.init();
        
        this.targetAttribute = getXFormsAttribute(TARGET_ATTRIBUTE);
        if (this.targetAttribute == null) {
            throw new XFormsBindingException("missing target attribute at " + this, this.target, null);
        }
     // two ways to get value one is with value attribute ohter <node>text<node>        
        this.valueAttribute = getXFormsAttribute(VALUE_ATTRIBUTE);
        if (this.valueAttribute == null) {
            Node child = this.element.getFirstChild();

            if ((child != null) && (child.getNodeType() == Node.TEXT_NODE)) {
                this.nodeValue = child.getNodeValue();
            }
            else {
                this.nodeValue = "";
            }
        }
    }        

	public void perform() throws XFormsException {
	    super.perform();
	    // create perform of new action
	    // ref have to show into error instance  
	    Instance instance = this.model.getInstance(getInstanceId());
        String pathExpression = getLocationPath();
        if (!instance.existsNode(pathExpression)) {
            getLogger().warn(this + " perform: nodeset '" + pathExpression + "' is empty");
            return;
        }
        String target = targetAttribute;
        System.out.println("Target: "+ target);
        String msg = valueAttribute;
        System.out.println("Error message: "+ msg);

            // set node value
         instance.setNodeValue(pathExpression, msg != null ? msg : "");
        
        doRefresh(true);
        DOMUtil.prettyPrintDOM(instance.getElement());

    }
		
	

}
