package com.idega.chiba.web.xml.xforms.functions;

import java.text.ParseException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xforms.xpath.XFormsExtensionFunctions;
import org.w3c.dom.Document;

import com.idega.chiba.web.xml.xforms.util.XFormsDateConverter;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.text.Item;
import com.idega.util.text.TableRecord;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.21 $ Last modified: $Date: 2009/03/18 08:31:44 $ by $Author: arunas $
 */
public class IdegaExtensionFunctions {

	private static final Logger LOGGER = Logger.getLogger(IdegaExtensionFunctions.class.getName());

	private IdegaExtensionFunctions() {}

	public static boolean userNameAlreadyExists(String userName) {
		return LoginDBHandler.isLoginInUse(userName);
	}

	public static Pointer instance(ExpressionContext expressionContext, String modelId, String instanceId) {
		if (StringUtil.isEmpty(modelId))
			return null;

		JXPathContext rootContext = expressionContext.getJXPathContext();
		Variables vars = rootContext.getVariables();

		vars.declareVariable("contextmodel", modelId);
		rootContext.setVariables(vars);

		return XFormsExtensionFunctions.instance(expressionContext, instanceId);
	}

	public static <T> T getValueFromExpression(ExpressionContext expContext, Instance instance, String exp, String params) throws XFormsException {
		return getValueFromExpression(expContext, instance, exp, params, Boolean.TRUE);
	}

	/**
	 * <p>Evaluates expression given in XForms, for example: idega:resolveTable.</p>
	 * @param expressionContext path to variable to be evaluated, for example:
	 * "Expression context [1] /data[1]/Applicant_name_fbc_66[1]" .
	 * @param instance
	 * @param expression is a name and method name of spring bean.
	 * @param params is variable names given for expression,
	 * usually in form "#{instance('data-instance')/Variable_name_fbc_123}",
	 * separated by comma.
	 * @param spaceOnError if <code>true</code> then on error
	 * {@link CoreConstants#SPACE} will be used, {@link CoreConstants#EMPTY} in
	 * {@link String} otherwise.
	 * @return
	 * @author <a href="mailto:martynas@idega.com">Martynas Stakė</a>
	 */
	public static <T> T getValueFromExpression(
			ExpressionContext expressionContext,
			Instance instance,
			String expression,
			String params,
			boolean spaceOnError
	) {
		String resolvedParams = null;
		if (!StringUtil.isEmpty(params)) {
			if (spaceOnError) {
				try {
					resolvedParams = expressionContext == null ?
								ExtensionFunctionUtil.resolveParams(instance, params) :
								ExtensionFunctionUtil.resolveParams(expressionContext, params);
				} catch (XFormsException e) {
					LOGGER.log(Level.WARNING, "Unable to resolve params.", e);
				}
			} else {
				resolvedParams = ExtensionFunctionUtil.resolveParams(expressionContext, instance, params);
			}
		}

		String resolveBeanExp = ExtensionFunctionUtil.formatExpression(expression, resolvedParams);

		try {
			@SuppressWarnings("unchecked")
			T value = (T) ELUtil.getInstance().evaluateExpression(resolveBeanExp);
			return value;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to evaluate expression: " + resolveBeanExp, e);
		}

		return null;
	}

	public static Object resolveExpression(ExpressionContext expressionContext, String exp, String params) throws XFormsException {
		try {
			Object value = getValueFromExpression(expressionContext, null, exp, params);

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

	/**
	 * <p>Usage: idega:resolveTable('someSpringBean.someMethod({0})',
	 * '#{instance(`data-instance`)/SomeXformsVariable}')</p>
	 * @param expressionContext
	 * @param exp 'someSpringBean.someMethod({0})' in example.
	 * @param params '#{instance(`data-instance`)/SomeXformsVariable}' in example.
	 * @return {@link org.w3.dom.Document} filled with
	 * {@link ExtensionFunctionUtil#createTableDocument(Collection)} or
	 * {@link IdegaExtensionFunctions#getValueFromExpression(ExpressionContext,
	 * Instance, String, String)} on failure.
	 * @throws XFormsException
	 */
	public static Object resolveTable(ExpressionContext expressionContext, String exp, String params) throws XFormsException {
		try {
			Object value = getValueFromExpression(expressionContext, null, exp,	params, Boolean.FALSE);

			if (value != null && (value instanceof Collection<?>)) {
				@SuppressWarnings("unchecked")
				final Collection<TableRecord> items = (Collection<TableRecord>) value;
				value = ExtensionFunctionUtil.createTableDocument(items);
			}

			return value;
		} catch (Exception e) {
			throw new XFormsException(e);
		}
	}

	public static Object resolveExpressionByInstance(Instance instance, String exp, String params) throws XFormsException {
		try {
			Object value = getValueFromExpression(null, instance, exp, params);

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

	public static Object resolveExpression(ExpressionContext expressionContext, String exp) throws XFormsException {
		return resolveExpression(expressionContext, exp, null);
	}

	/**
	 * @param expressionContext
	 * @param exp
	 * @param params
	 * @return document with elements of properties of resolved bean
	 * @throws XFormsException
	 */
	public static Object resolveBeanProperties(ExpressionContext expressionContext, String exp, String params) throws XFormsException {
		final Object bean = resolveExpression(expressionContext, exp, params);

		if (bean instanceof Document) {
			return bean;
		} else {
			try {
				return ExtensionFunctionUtil.createDocumentFromBeanProperties(bean);
			} catch (Exception e) {
				throw new XFormsException(e);
			}
		}
	}

	public static Object resolveBeanProperties(ExpressionContext expressionContext, String exp) throws XFormsException {
		return resolveBeanProperties(expressionContext, exp, null);
	}

	public static Object resolveBean(String exp) throws XFormsException {
		String beanExp = exp;
		exp = beanExp.substring(0, beanExp.lastIndexOf(CoreConstants.DOT));
		String methodName = beanExp.substring(beanExp.lastIndexOf(CoreConstants.DOT) + 1);

		Object value = ELUtil.getInstance().getBean(exp);
		try {
			if (value != null)
				value = PropertyUtils.getProperty(value, methodName);

			return value == null ? CoreConstants.EMPTY : value;
		} catch (Exception e) {
			throw new XFormsException("Unable to evaluate expression: " + exp, e);
		}
	}

	public static boolean isBeforeNow(String dateExp) throws XFormsException {
		try {
			IWTimestamp now = new IWTimestamp();
			now.setAsDate();

			return !StringUtil.isEmpty(dateExp) && new IWTimestamp(getxFormsDateConverter().convertStringFromXFormsToDate(dateExp))
				.isEarlierThan(now);
		} catch (ParseException e) {
			throw new XFormsException(e);
		}
	}

	public static boolean isBeforeOrEqualsNow(String dateExp) throws XFormsException {
		try {
			IWTimestamp now = new IWTimestamp();
			now.setAsDate();

			return !StringUtil.isEmpty(dateExp) && (new IWTimestamp(getxFormsDateConverter().convertStringFromXFormsToDate(dateExp))
				.isEarlierThan(now) || new IWTimestamp(getxFormsDateConverter().convertStringFromXFormsToDate(dateExp)).isEqualTo(now));
		} catch (ParseException e) {
			throw new XFormsException(e);
		}
	}

	public static boolean isAfterNow(String dateExp) throws XFormsException {
		try {
			IWTimestamp now = new IWTimestamp();
			now.setAsDate();

			return !StringUtil.isEmpty(dateExp) && new IWTimestamp(getxFormsDateConverter().convertStringFromXFormsToDate(dateExp))
				.isLaterThanOrEquals(now);
		} catch (ParseException e) {
			throw new XFormsException(e);
		}
	}

	public static String currentLocale() throws XFormsException {
		return CoreUtil.getIWContext().getCurrentLocale().toString();
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

	private static XFormsDateConverter getxFormsDateConverter() {
		return ELUtil.getInstance().getBean("xFormsDateConverter");
	}
}
