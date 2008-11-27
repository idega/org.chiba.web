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
import com.idega.util.CoreConstants;
import com.idega.util.expression.ELUtil;
import com.idega.util.text.Item;
import com.idega.util.xml.XmlUtil;
/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.6 $
 *
 * Last modified: $Date: 2008/11/27 03:09:14 $ by $Author: arunas $
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
   
    @SuppressWarnings("unchecked")
	public static Object resolveBean(String exp, String locale, String[] param)  throws XFormsException {
//    	this will be removed after bean resolving from expression
    	String beanExp = exp;
    	exp = beanExp.substring(0, beanExp.lastIndexOf(CoreConstants.DOT));
    	String methodName = beanExp.substring(beanExp.lastIndexOf(CoreConstants.DOT) + 1);
    	
//    	this will be using 
    	String formatedExp = MessageFormat.format(exp, (Object[])param);
    	
    	try {
    		DocumentBuilder documentBuilder = XmlUtil.getDocumentBuilder();
    		Document document = documentBuilder.newDocument();

//    	get data from bean epression is formatedExp
    	Object value = ELUtil.getInstance().getBean(exp);
    	
    		if (value != null){
    			
    			value = PropertyUtils.getProperty(value, methodName);
    			
    			if (value instanceof Collection<?>) {
    				
					Collection<Item> list = (Collection<Item>) value;
					
				
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
  
    
 
}