package com.idega.chiba.web.xml.xforms.functions;

import java.text.MessageFormat;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xforms.xpath.XFormsExtensionFunctions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.expression.ELUtil;
import com.idega.util.text.Item;
import com.idega.util.xml.XmlUtil;
/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.8 $
 *
 * Last modified: $Date: 2008/12/03 03:35:05 $ by $Author: arunas $
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
    
    private static final String item_node = "item";
    private static final String item_node_label = "itemLabel";
    private static final String item_value_label = "itemValue";
    private static final String item_attribute_label = "lang";    
    private static final String item_localizeEntries = "localizedEntries"; 
    private static final String exp_start = "#{";
    private static final String exp_end = "}";

    
    @SuppressWarnings("unchecked")
	public static Object resolveBean(String exp, String locale, String[] params)  throws XFormsException {

    	
    	String parameters = CoreConstants.EMPTY;
    	for (String param : params) {
    		parameters+="'" + param + "'"+ CoreConstants.SPACE; 
		}
    	exp = MessageFormat.format(exp, (Object[])parameters.split(CoreConstants.SPACE));
    	exp = new StringBuilder().append(exp_start).append(exp).append(exp_end).toString();
    	
    	try {
    		
    	Object value = ELUtil.getInstance().evaluateExpression(exp);
    	
    		if (value != null){
    			
    			if (value instanceof Collection<?>) {
    				
					Collection<Item> list = (Collection<Item>) value;
					
					DocumentBuilder documentBuilder = XmlUtil.getDocumentBuilder();
		    		Document document = documentBuilder.newDocument();
		    		
					Element localeElement = document.createElement(item_localizeEntries);
					localeElement.setAttribute(item_attribute_label, locale);
					Element itemElem = document.createElement(item_node);
					Element itemLabelElem = document.createElement(item_node_label);
					Element	itemValueElem = document.createElement(item_value_label);
					
					Node itemNode;
				
					for (Item item : list) {
						
						itemLabelElem.setTextContent(item.getItemLabel());
		    			itemValueElem.setTextContent(item.getItemValue());
						
		    			itemElem.appendChild(itemLabelElem);
		    			itemElem.appendChild(itemValueElem);
		    			
		    			itemNode = localeElement.getOwnerDocument().importNode(itemElem, true);
		        		
		    			localeElement.appendChild(itemNode);
		    			 
					}
					document.appendChild(localeElement);
					return document;
					
				}
    		}
    		
    		return value.toString();
			
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

   
}