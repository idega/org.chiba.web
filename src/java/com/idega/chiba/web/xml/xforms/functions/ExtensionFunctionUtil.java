package com.idega.chiba.web.xml.xforms.functions;

import java.text.MessageFormat;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.jxpath.ExpressionContext;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.idega.chiba.web.xml.xforms.util.XFormsUtil;
import com.idega.util.CoreConstants;
import com.idega.util.expression.ELUtil;
import com.idega.util.text.Item;
import com.idega.util.xml.XmlUtil;
/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2009/03/05 16:19:46 $ by $Author: arunas $
 */
public class ExtensionFunctionUtil {
	
	private static final String item_node = "item";
	private static final String item_node_label = "itemLabel";
	private static final String item_value_label = "itemValue";
	private static final String items = "items"; 
	
	public static Document createItemListDocument (Collection<Item> list) {
		
		try {
			
			DocumentBuilder documentBuilder = XmlUtil.getDocumentBuilder();
			Document document = documentBuilder.newDocument();
			
			Element localeElement = document.createElement(items);
						
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
			
		} catch (ParserConfigurationException e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public static final String apostrophe ="'";
	public static final String splitter ="_#,";
	
	public static String resolveParams(ExpressionContext expressionContext, String paramsExp) throws XFormsException{
		
		Instance instance;
    	String instanceID = CoreConstants.EMPTY;
    	String [] params = paramsExp.split(CoreConstants.COMMA);
    	StringBuilder resolvedParamsExp = new StringBuilder(); 

    	Object value;
    	
    	for (String param : params) {
    		if (param.contains(ELUtil.EXPRESSION_BEGIN)) {
        		
        		param = ELUtil.cleanupExp(param);

        		instanceID = param.split("`")[1];
        	 	param = param.replaceAll("`", apostrophe);
        	 	instance = XFormsUtil.getInstance(expressionContext, instanceID);
        	 	
        	 	value = XFormsUtil.getValueFromExpression(param, instance);
        	 	
        	 	resolvedParamsExp.append(apostrophe).append(value).append(apostrophe).append(splitter);

        	} else {
        		resolvedParamsExp.append(apostrophe).append(param).append(apostrophe).append(splitter);
        	}
        	
		}
    	
		return resolvedParamsExp.toString();	
	}
	
	public static String formatExpression(String elExpression, String paramsExpression){
		
    	String exp = MessageFormat.format(elExpression, (Object[])paramsExpression.split(splitter));
    	exp = new StringBuilder().append(ELUtil.EXPRESSION_BEGIN).append(exp).append(ELUtil.EXPRESSION_END).toString();
		return exp;
	}

}
