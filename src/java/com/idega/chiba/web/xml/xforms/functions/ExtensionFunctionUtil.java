package com.idega.chiba.web.xml.xforms.functions;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.jxpath.ExpressionContext;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.idega.chiba.web.xml.xforms.util.XFormsUtil;
import com.idega.util.CoreConstants;
import com.idega.util.ListUtil;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.text.Item;
import com.idega.util.text.TableRecord;
import com.idega.util.xml.XmlUtil;
/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.3 $
 *
 * Last modified: $Date: 2009/03/17 09:02:04 $ by $Author: arunas $
 */
public class ExtensionFunctionUtil {
	
	private static final String item_node = "item";
	private static final String item_node_label = "itemLabel";
	private static final String item_value_label = "itemValue";
	private static final String items = "items";
	private static final String tableRow = "tableRow";
	private static final Logger LOGGER = Logger.getLogger(ExtensionFunctionUtil.class.getName());

	private static final Object[] emptyArray = new Object[0];

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
	
	/**
	 * <p>Creates {@link org.w3.dom.Document} with structure 
	 * <\tableRow><\ColumName>Value<\/ColumName><\/tableRow>.
	 * ColumnName's are parsed from 
	 * {@link com.idega.util.text.TableRecord#getItems()}.
	 * </p>
	 * @param list {@link Collection} of {@link com.idega.util.text.TableRecord}.
	 * @return {@link org.w3.dom.Document} or null if error happen.
	 */
	public static Document createTableDocument (Collection<TableRecord> list) {
		if (ListUtil.isEmpty(list)) {
			return null;
		}

		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = XmlUtil.getDocumentBuilder();
		} catch (ParserConfigurationException e) {
			LOGGER.log(Level.WARNING, "Unable to create table document.", e);
			return null;
		}

		if (documentBuilder == null) {
			return null;
		}

		Document document = documentBuilder.newDocument();
		if (document == null) {
			return null;
		}

		Element localeElement = document.createElement(items);			
		if (localeElement == null) {
			return null;
		}

		for (TableRecord tableRecord : list) {
			Element itemElem = document.createElement(tableRow);
			if (itemElem == null) {
				continue;
			}

			Map<String, String> data = tableRecord.getItems();
			if (MapUtil.isEmpty(data)) {
				continue;
			}

			for (Iterator<Map.Entry<String, String>> iter = data.entrySet().iterator(); iter.hasNext();) {
				Map.Entry<String, String> entry = iter.next();
				if (entry == null) {
					continue;
				}

				String key = entry.getKey();
				String value = entry.getValue();
				if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
					continue;
				}

				Element	itemValueElem = document.createElement(key);
				if (itemValueElem == null) {
					continue;
				}

				itemValueElem.setTextContent(value);
				itemElem.appendChild(itemValueElem);

			}

			Document documentOfItemNode = localeElement.getOwnerDocument();
			if (documentOfItemNode == null) {
				continue;
			}

			Node itemNode = documentOfItemNode.importNode(itemElem, true);
			if (itemNode == null) {
				continue;
			}

			localeElement.appendChild(itemNode);
		}

		document.appendChild(localeElement);
		return document;
	}
	
	/**
	 * <p>Takes value from given XForms variable.</p>
	 * @param expressionContext path to variable to be evaluated, for example: 
	 * "Expression context [1] /data[1]/Applicant_name_fbc_66[1]" .
	 * @param instance
	 * @param paramsExp is variable names given for expression, 
	 * usually in form "#{instance('data-instance')/Variable_name_fbc_123}",.. 
	 * separated by comma.
	 * @return Xforms variable values separated by comma. For example:
	 * paramsExp - #{instance(`data-instance`)/Applicant_name_fbc_53},...
	 * will return '11', '',... as answer, where 11 is value of given Xforms variable, 
	 * '' - means no value found or it is value.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public static String resolveParams(ExpressionContext expressionContext, 
			Instance instance, String paramsExp) {
		return resolveParams(expressionContext, instance, paramsExp, 
				Boolean.FALSE);
	}
	
	/**
	 * <p>Takes value from given XForms variable.</p>
	 * @param expressionContext path to variable to be evaluated, for example: 
	 * "Expression context [1] /data[1]/Applicant_name_fbc_66[1]" .
	 * @param instance
	 * @param paramsExp is variable names given for expression, 
	 * usually in form "#{instance('data-instance')/Variable_name_fbc_123}",.. 
	 * separated by comma.
	 * @param returnsSpaceOnFailure if set to <code>true</code>, then returns 
	 * {@link CoreConstants#SPACE} on failure, otherwise adds 
	 * {@link CoreConstants#EMPTY} to places in string, where failures found.
	 * @return Xforms variable values separated by comma. For example:
	 * paramsExp - #{instance(`data-instance`)/Applicant_name_fbc_53},...
	 * will return '11', '',... as answer, where 11 is value of given Xforms variable.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	private static String resolveParams(ExpressionContext expressionContext, 
			Instance instance, String paramsExp, boolean returnsSpaceOnFailure) {

		String [] params = paramsExp.split(CoreConstants.COMMA);
    		StringBuilder resolvedParamsExp = new StringBuilder();

    		Object value = null;

    		for (String param : params) {
    			if (param.contains(ELUtil.EXPRESSION_BEGIN)) {
    				param = ELUtil.cleanupExp(param.trim());
     
    				try {
    					if (expressionContext != null) {
    						value = getInstanceValueFromExpression(expressionContext, param);
    					} else if (instance != null) {
    						value = getInstanceValueFromExpression(instance, param);
    					} else {
    						value = CoreConstants.EMPTY;
    					}
    				} catch (XFormsException e) {
    					LOGGER.log(Level.WARNING, "Unable to get value from parameter: " + param, e);
    				}	
        		
    				if (value == null || value.toString().equals(CoreConstants.EMPTY)) {
    					value = CoreConstants.EMPTY;
    				}
    				
    				if (returnsSpaceOnFailure) {
    					if (value == null || value.toString().equals(CoreConstants.EMPTY)) {
    	        	 			return CoreConstants.SPACE;
    					}
    				}
    				
    			} else {
    				value = param;
    			}
        	
    			resolvedParamsExp.append(CoreConstants.QOUTE_SINGLE_MARK)
    				.append(value)
    				.append(CoreConstants.QOUTE_SINGLE_MARK)
    				.append(CoreConstants.JS_STR_PARAM_SEPARATOR);    		
    		}
    	
		return resolvedParamsExp.toString();
	}
	
	/**
	 * <p>Takes value from given XForms variable.</p>
	 * @param expressionContext path to variable to be evaluated, for example: 
	 * "Expression context [1] /data[1]/Applicant_name_fbc_66[1]" .
	 * @param paramsExp is variable names given for expression, 
	 * usually in form "#{instance('data-instance')/Variable_name_fbc_123}",.. 
	 * separated by comma.
	 * @return Xforms variable values separated by comma. For example:
	 * paramsExp - #{instance(`data-instance`)/Applicant_name_fbc_53},...
	 * will return '11',... as answer, where 11 is value of given Xforms variable. 
	 * Returns {@link CoreConstants#SPACE} on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public static String resolveParams(ExpressionContext expressionContext, String paramsExp) throws XFormsException {
		return resolveParams(expressionContext, null, paramsExp, Boolean.TRUE);
	}
	
	/**
	 * <p>Takes value from given XForms variable.</p>
	 * @param instance
	 * @param paramsExp is variable names given for expression, 
	 * usually in form "#{instance('data-instance')/Variable_name_fbc_123}",.. 
	 * separated by comma.
	 * @return Xforms variable values separated by comma. For example:
	 * paramsExp - #{instance(`data-instance`)/Applicant_name_fbc_53},...
	 * will return '11',... as answer, where 11 is value of given Xforms variable. 
	 * Returns {@link CoreConstants#SPACE} on failure.
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public static String resolveParams(Instance instance, String paramsExp) throws XFormsException {
		return resolveParams(null, instance, paramsExp, Boolean.TRUE);
	}
	
	public static String formatExpression(String elExpression, String paramsExpression) {
    	String exp = MessageFormat.format(elExpression, paramsExpression != null? (Object[])paramsExpression.split(CoreConstants.JS_STR_PARAM_SEPARATOR) : emptyArray);
    	exp = new StringBuilder().append(ELUtil.EXPRESSION_BEGIN).append(exp).append(ELUtil.EXPRESSION_END).toString();
		return exp;
	}
	
	public static Instance getInstance(ExpressionContext expressionContext, String exp) {
		String instanceID = exp.split("`")[1];
		return XFormsUtil.getInstance(expressionContext, instanceID);
	}
	
	public static Object getInstanceValueFromExpression(Instance instance, String exp) throws XFormsException {
	 	exp = exp.replaceAll("`", CoreConstants.QOUTE_SINGLE_MARK);
	 	Object value = XFormsUtil.getValueFromExpression(exp, instance);
		return value;
	}
	
	private static Object getInstanceValueFromExpression(ExpressionContext expressionContext,String exp)  throws XFormsException{
		
		String instanceID = exp.split("`")[1];
	 	exp = exp.replaceAll("`", CoreConstants.QOUTE_SINGLE_MARK);
	 	
	 	Instance instance = XFormsUtil.getInstance(expressionContext, instanceID);
	 	Object value = XFormsUtil.getValueFromExpression(exp, instance);
		
		return value;
	}
	
	public static Document createDocumentFromBeanProperties(Object bean) {
		
		try {
			
			final DocumentBuilder documentBuilder = XmlUtil.getDocumentBuilder();
			final Document document = documentBuilder.newDocument();
			
			final Element rootElement = (Element)document.appendChild(document.createElement("result"));
			
			final Field[] fields = bean.getClass().getDeclaredFields();
			
			for (Field field : fields) {
	            
				try {
					
					final Object val = BeanUtils.getProperty(bean, field.getName());
					
					final Element el = document.createElement(field.getName());
					rootElement.appendChild(el);
					
					if(val != null) {
					
						el.setTextContent(String.valueOf(val));
					}
	                
                } catch (Exception e) {
	                // skipping field
                	Logger.getLogger(ExtensionFunctionUtil.class.getName()).log(Level.SEVERE, "Error while parsing bean property, and setting it to resulting document. Skipping.", e);
                }
            }
			
			return document;
			
		} catch (ParserConfigurationException e) {
			Logger.getLogger(ExtensionFunctionUtil.class.getName()).log(Level.SEVERE, "Err", e);
		}
		return null;
	}
}
