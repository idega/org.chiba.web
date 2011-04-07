package com.idega.chiba.web.xml.xforms.functions;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;
import org.apache.ojb.broker.cache.RuntimeCacheException;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xforms.xpath.XFormsExtensionFunctions;
import org.w3c.dom.Document;

import com.idega.chiba.web.xml.xforms.util.XFormsDateConverter;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.text.Item;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.21 $ Last modified: $Date: 2009/03/18 08:31:44 $ by $Author: arunas $
 */
public class IdegaExtensionFunctions {
	
	private IdegaExtensionFunctions() {
	}
	
	public static boolean userNameAlreadyExists(String userName) {
		
		return LoginDBHandler.isLoginInUse(userName);
	}
	
	public static Pointer instance(ExpressionContext expressionContext,
	        String modelId, String instanceId) {
		
		if (modelId != null && modelId.length() != 0) {
			
			JXPathContext rootContext = expressionContext.getJXPathContext();
			Variables vars = rootContext.getVariables();
			
			vars.declareVariable("contextmodel", modelId);
			rootContext.setVariables(vars);
			
			return XFormsExtensionFunctions.instance(expressionContext,
			    instanceId);
		}
		
		return null;
	}
	
	public static Object resolveExpression(ExpressionContext expressionContext,
	        String exp, String params) throws XFormsException {
		
		final String resolvedParams = params != null ? ExtensionFunctionUtil
		        .resolveParams(expressionContext, params) : null;
		final String resolveBeanExp = ExtensionFunctionUtil.formatExpression(
		    exp, resolvedParams);
		
		try {
			
			final Object value = ELUtil.getInstance().evaluateExpression(
			    resolveBeanExp);
			
			if (value != null && (value instanceof Collection<?>)) {
				@SuppressWarnings("unchecked")
				final Collection<Item> items = (Collection<Item>) value;
				return ExtensionFunctionUtil.createItemListDocument(items);
			}
			
			return value;
			
		} catch (Exception e) {
			throw new XFormsException(e);
		}
	}
	
	public static Object resolveExpressionByInstance(Instance instance,
	        String exp, String params) throws XFormsException {
		
		final String resolvedParams = params != null ? ExtensionFunctionUtil
		        .resolveParams(instance, params) : null;
		final String resolveBeanExp = ExtensionFunctionUtil.formatExpression(
		    exp, resolvedParams);
		
		try {
			
			final Object value = ELUtil.getInstance().evaluateExpression(
			    resolveBeanExp);
			
			if (value != null && (value instanceof Collection<?>)) {
				@SuppressWarnings("unchecked")
				final Collection<Item> items = (Collection<Item>) value;
				return ExtensionFunctionUtil.createItemListDocument(items);
			}
			
			return value;
			
		} catch (Exception e) {
			throw new XFormsException(e);
		}
	}
	
	public static Object resolveExpression(ExpressionContext expressionContext,
	        String exp) throws XFormsException {
		
		return resolveExpression(expressionContext, exp, null);
	}
	
	/**
	 * @param expressionContext
	 * @param exp
	 * @param params
	 * @return document with elements of properties of resolved bean
	 * @throws XFormsException
	 */
	public static Object resolveBeanProperties(
	        ExpressionContext expressionContext, String exp, String params)
	        throws XFormsException {
		
		final Object bean = resolveExpression(expressionContext, exp, params);
		
		if (bean instanceof Document) {
			return bean;
		} else {
			
			try {
				return ExtensionFunctionUtil
				        .createDocumentFromBeanProperties(bean);
			} catch (Exception e) {
				throw new XFormsException(e);
			}
		}
	}
	
	public static Object resolveBeanProperties(
	        ExpressionContext expressionContext, String exp)
	        throws XFormsException {
		return resolveBeanProperties(expressionContext, exp, null);
	}
	
	public static Object resolveBean(String exp) throws XFormsException {
		
		String beanExp = exp;
		exp = beanExp.substring(0, beanExp.lastIndexOf(CoreConstants.DOT));
		String methodName = beanExp.substring(beanExp
		        .lastIndexOf(CoreConstants.DOT) + 1);
		
		Object value = ELUtil.getInstance().getBean(exp);
		
		try {
			if (value != null)
				value = PropertyUtils.getProperty(value, methodName);
			
			return value == null ? CoreConstants.EMPTY : value;
			
		} catch (Exception e) {
			throw new XFormsException(e);
		}
	}
	
	public static boolean isBeforeNow(String dateExp) throws XFormsException {
		
		try {
			return !StringUtil.isEmpty(dateExp)
			        && getxFormsDateConverter().convertStringFromXFormsToDate(
			            dateExp).before(new Date());
			
		} catch (ParseException e) {
			throw new RuntimeCacheException(e);
		}
	}
	
	public static boolean isAfterNow(String dateExp) throws XFormsException {
		
		try {
			return !StringUtil.isEmpty(dateExp)
			        && getxFormsDateConverter().convertStringFromXFormsToDate(
			            dateExp).after(new Date());
			
		} catch (ParseException e) {
			throw new RuntimeCacheException(e);
		}
	}
	
	public static String currentLocale() throws XFormsException {
		
		return IWContext.getInstance().getCurrentLocale().toString();
	}
	
	public static boolean hasItem(String list, String elem)
	        throws XFormsException {
		
		String[] items = list.split(CoreConstants.SPACE);
		
		for (String item : items)
			if (item.equals(elem))
				return true;
		
		return false;
	}
	
	public static String upperCase(String value) throws XFormsException {
		
		return value.toUpperCase();
	}
	
	private static XFormsDateConverter getxFormsDateConverter() {
		
		return ELUtil.getInstance().getBean("xFormsDateConverter");
	}
	
}