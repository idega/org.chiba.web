package com.idega.chiba.web.xml.xforms.ui.state;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.xforms.core.ModelItem;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xforms.ui.BindingElement;
import org.chiba.xml.xforms.ui.state.BoundElementState;
import org.chiba.xml.xforms.ui.state.UIElementStateUtil;
import org.w3c.dom.Element;

import com.idega.chiba.web.xml.xforms.ui.IdegaOutput;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;

public class IdegaBoundElementState extends BoundElementState {

	private BindingElement owner;

	private boolean[] currentProperties;
    private Object currentValue;
    private String currentType;

	@Override
	public void setOwner(BindingElement owner) {
		this.owner = owner;
		super.setOwner(owner);
	}

	@Override
	public void update() throws XFormsException {
		if (owner instanceof IdegaOutput) {
			IdegaOutput output = (IdegaOutput) owner;
			String resolver = output.getResolver();
			if (StringUtil.isEmpty(resolver)) {
				super.update();
				return;
			}

			String expression = "#{" + resolver + "}";

			ModelItem modelItem = UIElementStateUtil.getModelItem(this.owner);
			boolean[] properties = UIElementStateUtil.getModelItemProperties(modelItem);

			// update properties
			setProperties(properties);

			// update types
			String datatype = modelItem != null ? UIElementStateUtil.getDatatype(modelItem, owner.getElement()) : null;
			String p3ptype = modelItem != null ? modelItem.getDeclarationView().getP3PType() : null;
			UIElementStateUtil.setStateAttribute(state, TYPE_ATTRIBUTE, datatype);
			UIElementStateUtil.setStateAttribute(state, P3PTYPE_ATTRIBUTE, p3ptype);
			currentType = datatype;

		    // update value
			Object value = null;
		    //attempt to store the subtree (xforms:copy)
			boolean updated = false;
		    if (modelItem != null && modelItem.getNode() instanceof Element) {
		    	value = storeSubtree(modelItem);
		    	Element childElement = DOMUtil.getFirstChildElement((Element)modelItem.getNode());
		    	updated = childElement != null;
		    }
		    if (!updated) {
		    	//otherwise fallback to a string value
	    		value = modelItem != null ? modelItem.getValue() : null;
	    		if (value != null && !((value instanceof java.lang.String && StringUtil.isEmpty((String) value)))) {
	    			expression = StringHandler.replace(expression, "{0}", "'" + value + "'");
	    			
	    			try {
						value = ELUtil.getInstance().evaluateExpression(expression);
					} catch (Exception e) {
						Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error while evaluating expression: " + expression, e);
					}
	    		}
	    		DOMUtil.setElementValue(state, (String) value);
		    }

		    // dispatch xforms events
		    UIElementStateUtil.dispatchXFormsEvents(owner, modelItem, properties);
            // dispatch property, value, and type change events because the owner's external value change caused this update
		    UIElementStateUtil.dispatchChibaEvents(owner, currentProperties, currentValue, currentType, properties, value, datatype);

		    //store properties and value
		    this.currentProperties = properties;
		    this.currentValue = value;

		    return;
		}

		super.update();
	}

	private void setProperties(boolean[] properties) {
		// set properties
        UIElementStateUtil.setStateAttribute(this.state, VALID_PROPERTY, String.valueOf(properties[UIElementStateUtil.VALID]));
        UIElementStateUtil.setStateAttribute(this.state, READONLY_PROPERTY, String.valueOf(properties[UIElementStateUtil.READONLY]));
        UIElementStateUtil.setStateAttribute(this.state, REQUIRED_PROPERTY, String.valueOf(properties[UIElementStateUtil.REQUIRED]));
        UIElementStateUtil.setStateAttribute(this.state, ENABLED_PROPERTY, String.valueOf(properties[UIElementStateUtil.ENABLED]));
    }

}