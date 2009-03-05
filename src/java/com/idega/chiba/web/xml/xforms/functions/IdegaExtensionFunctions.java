package com.idega.chiba.web.xml.xforms.functions;

import java.util.Collection;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xforms.xpath.XFormsExtensionFunctions;

import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.expression.ELUtil;
import com.idega.util.text.Item;
/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.19 $
 *
 * Last modified: $Date: 2009/03/05 16:19:46 $ by $Author: arunas $
 */
public class IdegaExtensionFunctions {

    private IdegaExtensionFunctions() {
    }

    public static boolean userNameAlreadyExists(String userName) {
   
    	return LoginDBHandler.isLoginInUse(userName);
    }
    
    public static Pointer instance(ExpressionContext expressionContext, String modelId, String instanceId){
    	
    	if(modelId != null && modelId.length() != 0) {
    		
    		JXPathContext rootContext = expressionContext.getJXPathContext();
    		Variables vars = rootContext.getVariables();
    		
    		vars.declareVariable("contextmodel", modelId);
    		rootContext.setVariables(vars);
    		
    		return XFormsExtensionFunctions.instance(expressionContext, instanceId);
    	}
    	
    	return null;
    }
    
   
    @SuppressWarnings("unchecked")
	public static Object resolveBean(String exp, String[] params)  throws XFormsException {

    	if (params.length == 0 || params == null)
    		return CoreConstants.EMPTY;
    	
    	StringBuilder parametersExp = new StringBuilder(); 
    	
    	for (String param : params) 
    		  parametersExp.append(ExtensionFunctionUtil.apostrophe).append(param).append(ExtensionFunctionUtil.apostrophe).append(ExtensionFunctionUtil.splitter);
    	
    	ExtensionFunctionUtil.formatExpression(exp, parametersExp.toString());
    	
    	try {
	
    	Object value = ELUtil.getInstance().evaluateExpression(exp);
    	
    		if (value != null){
    			
    			if (value instanceof Collection<?>) {
    				
					Collection<Item> list = (Collection<Item>) value;
					
					return ExtensionFunctionUtil.createItemListDocument(list);
					
				}
    		}
    		
    		return value;
			
		} catch (Exception e) {
			throw new XFormsException(e);
		}
    	
    }
    
    @SuppressWarnings("unchecked")
    public static Object resolveExpression(ExpressionContext expressionContext, String exp, String params) throws XFormsException {
    	
    	String resolvedParams = ExtensionFunctionUtil.resolveParams(expressionContext, params);
    	String resolveBeanExp = ExtensionFunctionUtil.formatExpression(exp, resolvedParams);
    	
    	try {
    		
        	Object value = ELUtil.getInstance().evaluateExpression(resolveBeanExp);
        	
        		if (value != null){
        			
        			if (value instanceof Collection<?>) {
        				
    					Collection<Item> list = (Collection<Item>) value;
    					
    					return ExtensionFunctionUtil.createItemListDocument(list);
    					
    				}
        		}
        		
        		return value;
    			
    		} catch (Exception e) {
    			throw new XFormsException(e);
    		}
    	
    }
    
    public static Object resolveBean(String exp)  throws XFormsException {
    	
    	String beanExp = exp;
    	exp = beanExp.substring(0, beanExp.lastIndexOf(CoreConstants.DOT));
    	String methodName = beanExp.substring(beanExp.lastIndexOf(CoreConstants.DOT) + 1);
    	
    	Object value = ELUtil.getInstance().getBean(exp);

    	try {
    		if (value != null)
    			value = PropertyUtils.getProperty(value, methodName);
    		
    		return value == null ? CoreConstants.EMPTY : value;
    		
		} catch (Exception e) {
			throw new XFormsException(e);
		}
    }
    
    public static String currentLocale() throws XFormsException{
    	
    	String locale = IWContext.getInstance().getCurrentLocale().toString();
    	
    	return locale;
    }
    
    public static boolean hasItem(String list, String elem) throws XFormsException {
    	
    	String[] items = list.split(CoreConstants.SPACE);
    	
    	for (String item : items) 
			if (item.equals(elem)) 
					return true;
			
		
    	return false;	
    }
    
    public static String upperCase(String value) throws XFormsException {
    	
    	return value.toUpperCase();
    	
    }

   
}