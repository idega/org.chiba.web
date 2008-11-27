package com.idega.chiba.web.xml.xforms.elements.action;

import org.apache.commons.jxpath.JXPathContext;
import org.chiba.xml.xforms.action.SetValueAction;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsComputeException;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class IdegaSetValueAction extends SetValueAction{
	
	    private String nodeValue;
	    private String valueAttribute;

	  

	public IdegaSetValueAction(Element element, Model model) {
		super(element, model);
	}
	
	 /**
     * Performs the <code>setvalue</code> action.
     *
     * @throws XFormsException if an error occurred during <code>setvalue</code>
     * processing.
     */
	
	   public void init() throws XFormsException {
	        super.init();
	        
	        this.valueAttribute = getXFormsAttribute(VALUE_ATTRIBUTE);
	        if (this.valueAttribute == null) {
	            org.w3c.dom.Node child = this.element.getFirstChild();

	            if ((child != null) && (child.getNodeType() == Node.TEXT_NODE)) {
	                this.nodeValue = child.getNodeValue();
	            }
	            else {
	                this.nodeValue = "";
	            }
	        }
	    }
	
	@Override
    public void perform() throws XFormsException {
		
        super.perform();
        
        // get instance and nodeset information
        Instance instance = this.model.getInstance(getInstanceId());
        String pathExpression = getLocationPath();
        if (!instance.existsNode(pathExpression)) {
            getLogger().warn(this + " perform: nodeset '" + pathExpression + "' is empty");
            return;
        }

        if (valueAttribute != null) {
            // since jxpath doesn't provide a means for evaluating an expression
            // in a certain context, we use a trick here: the expression will be
            // evaluated during getPointer and the result stored as a variable
            JXPathContext context = instance.getInstanceContext();

            String currentPath = getParentContextPath(this.element);
            context.getVariables().declareVariable("currentContextPath", currentPath);
            try {
                context.getPointer(pathExpression + "[chiba:declare('node-value', " + this.valueAttribute + ")]");
            }
            catch (Exception e) {
                throw new XFormsComputeException("invalid value expression at " + this, e, this.target, this.valueAttribute);
            }
            Object value = context.getValue("chiba:undeclare('node-value')");

            // check for string conversion to prevent sth. like "5 + 0" to be evaluated to "5.0"
            if (value instanceof Double) {
                // additionaly check for special cases
                double doubleValue = ((Double) value).doubleValue();
                if (!(Double.isNaN(doubleValue) || Double.isInfinite(doubleValue))) {
                    value = context.getValue("string(" + value + ")");
                }
            }
            context.getVariables().undeclareVariable("currentContextPath");

            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this + " perform: setting evaluated value '" + value + "'");
            }

            // set node value
            if (value instanceof Document) {
				Document xml_doc = (Document) value;
				instance.setNode(pathExpression, xml_doc.getDocumentElement());
			} else {
				instance.setNodeValue(pathExpression, value != null ? value.toString() : "");
			}
            
        }
        else {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this + " perform: setting literal value '" + this.nodeValue + "'");
            }

            // set node value
            instance.setNodeValue(pathExpression, this.nodeValue);
        }

        // update behaviour
        doRecalculate(true);
        doRevalidate(true);
        doRefresh(true);
    }

}
