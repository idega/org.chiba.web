package com.idega.chiba.web.xml.xforms.util;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.xerces.dom.NodeImpl;
import org.chiba.xml.xforms.XFormsElement;
import org.chiba.xml.xforms.action.AbstractAction;
import org.chiba.xml.xforms.core.BindingResolver;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.exception.XFormsComputeException;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xforms.ui.AbstractFormControl;
import org.chiba.xml.xforms.ui.BindingElement;
import org.w3c.dom.Node;

/**
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/10/06 11:21:23 $ by $Author: civilis $
 *
 */
public class XFormsUtil {

	/**
	 * copied from chiba Output implementation
	 * @param valueAttribute - value attribute to compute from
	 * @param action - the action this method is called from
	 * @return
	 * @throws XFormsException
	 */
	public static Object getValueFromExpression(String valueAttribute, AbstractAction action) throws XFormsException {
		
        String pathExpression = BindingResolver.getExpressionPath(action, action.getRepeatItemId());
        Instance instance = action.getModel().getInstance(action.getModel().computeInstanceId(pathExpression));
        if (!instance.existsNode(pathExpression)) {
            return null;
        }

        // todo: implement XPathProcessor abstraction, this code is copied from SetValueAction
        // since jxpath doesn't provide a means for evaluating an expression
        // in a certain context, we use a trick here: the expression will be
        // evaluated during getPointer and the result stored as a variable
        JXPathContext context = instance.getInstanceContext();

        String currentPath = getParentContextPath(action.getElement());
        context.getVariables().declareVariable("currentContextPath", currentPath);
        context.getVariables().declareVariable("contextmodel", action.getModel().getId());
        try {
            context.getPointer(pathExpression + "[chiba:declare('output-value', " + valueAttribute + ")]");
        }
        catch (Exception e) {
            throw new XFormsComputeException("invalid value expression at " + action, e, action.getTarget(), valueAttribute);
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
	
	public static Object getValueFromExpression(String valueAttribute, AbstractFormControl formControl) throws XFormsException {
        String pathExpression = BindingResolver.getExpressionPath(formControl, formControl.getRepeatItemId());
        Instance instance = formControl.getModel().getInstance(formControl.getModel().computeInstanceId(pathExpression));
        if (!instance.existsNode(pathExpression)) {
            return null;
        }

        // todo: implement XPathProcessor abstraction, this code is copied from SetValueAction
        // since jxpath doesn't provide a means for evaluating an expression
        // in a certain context, we use a trick here: the expression will be
        // evaluated during getPointer and the result stored as a variable
        JXPathContext context = instance.getInstanceContext();

        String currentPath = getParentContextPath(formControl.getElement());
        context.getVariables().declareVariable("currentContextPath", currentPath);
        context.getVariables().declareVariable("contextmodel", formControl.getModel().getId());
        try {
            context.getPointer(pathExpression + "[chiba:declare('output-value', " + valueAttribute + ")]");
        }
        catch (Exception e) {
        	throw new XFormsComputeException("invalid value expression at " + formControl, e, formControl.getTarget(), valueAttribute);
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
	
	public static Pointer getPointerFromExpression(String valueAttribute, AbstractAction action) throws XFormsException {
		
        String pathExpression = BindingResolver.getExpressionPath(action, action.getRepeatItemId());
        Instance instance = action.getModel().getInstance(action.getModel().computeInstanceId(pathExpression));
        if (!instance.existsNode(pathExpression)) {
            return null;
        }

        // todo: implement XPathProcessor abstraction, this code is copied from SetValueAction
        // since jxpath doesn't provide a means for evaluating an expression
        // in a certain context, we use a trick here: the expression will be
        // evaluated during getPointer and the result stored as a variable
        JXPathContext context = instance.getInstanceContext();

        String currentPath = getParentContextPath(action.getElement());
        context.getVariables().declareVariable("currentContextPath", currentPath);
        context.getVariables().declareVariable("contextmodel", action.getModel().getId());
        try {
            return context.getPointer(pathExpression + "[chiba:declare('output-value', " + valueAttribute + ")]");
        }
        catch (Exception e) {
            throw new XFormsComputeException("invalid value expression at " + action, e, action.getTarget(), valueAttribute);
        }
    }
	
	public static String getParentContextPath(Node start){
        String result=null;
        NodeImpl parent = (NodeImpl) start.getParentNode();
        if(parent==null) return null;

        Object o =  parent.getUserData();
        if(o instanceof XFormsElement){
            XFormsElement xCurrent = (XFormsElement) parent.getUserData();
            if(xCurrent == null){
                getParentContextPath(parent);
            }

            if(xCurrent instanceof BindingElement){
                if(((BindingElement)xCurrent).isBound()){
                    String locationPath = ((BindingElement)xCurrent).getLocationPath();
                    return locationPath;
                }else{
                    result=getParentContextPath(parent.getParentNode());
                }
            }
        }else{
            return getParentContextPath(parent);
        }
        return result;
    }
}