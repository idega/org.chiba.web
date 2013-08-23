package com.idega.chiba.web.xml.xforms.connector.context;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.chiba.xml.xforms.connector.URIResolver;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;

import com.idega.chiba.web.xml.xforms.connector.context.beans.ChoiceListData;
import com.idega.chiba.web.xml.xforms.connector.context.beans.LocalizedEntries;
import com.idega.util.CoreUtil;
import com.idega.util.StringUtil;
import com.idega.util.datastructures.map.MapUtil;
import com.idega.util.text.Item;
import com.idega.util.xml.XmlUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2009/05/15 10:52:53 $ by $Author: valdas $
 */
public class ContextXmlResolver extends org.chiba.xml.xforms.connector.context.ContextResolver implements URIResolver {

	private static final Logger LOGGER = Logger.getLogger(ContextXmlResolver.class.getName());

	private static final String faces_exp_part1 = "#{";
	private static final String faces_exp_part2 = "}";

	/**
	 * resolves object, which is configured in the faces-config.xml, method value
	 */
	@Override
	public Object resolve() throws XFormsException {
        try {
	        return createResponseDocument(resolveItems(getURI()));
        } catch (Exception e) {
        	LOGGER.log(Level.WARNING, "Error resolving items", e);
        	try {
        		return XmlUtil.getDocumentBuilder().newDocument();
			} catch (Exception e2) {
				throw new XFormsException(e2);
			}
        }
    }

    protected Document createResponseDocument(Map<Locale, Map<String, String>> localizedItems) throws Exception {
    	if (MapUtil.isEmpty(localizedItems)) {
    		return null;
    	}

    	ChoiceListData choiceListData = new ChoiceListData();
		for (Entry<Locale, Map<String, String>> localizedItemsEntry : localizedItems.entrySet()) {
			LocalizedEntries localizedEntries = new LocalizedEntries();
			localizedEntries.setLang(localizedItemsEntry.getKey().toString());

			for (Entry<String, String> itemEntry : localizedItemsEntry.getValue().entrySet()) {
				localizedEntries.add(new Item(itemEntry.getKey(), itemEntry.getValue()));
			}

			choiceListData.add(localizedEntries);
		}

        return choiceListData.getDocument();
	}

    public Map<Locale, Map<String, String>> resolveItems(String uri) {
    	if (StringUtil.isEmpty(uri)) {
    		LOGGER.warning("No URI was given. Nothing to resolve.");
    		return null;
    	}

    	try {
	    	setURI(uri);

	    	String xpath = null;
			try {
				xpath = new URI(getURI()).getSchemeSpecificPart();
			} catch (URISyntaxException e) {
				LOGGER.log(Level.WARNING, "Bad URI was given: " + getURI() + ". No items will be resolved.", e);
			}

	        FacesContext ctx = FacesContext.getCurrentInstance();
	        if (ctx == null) {
	        	LOGGER.warning("Unable to get: " + FacesContext.class.getName() + ". No items will be resolved for URI: " + uri);
	        	return null;
	        }

	        ELContext elContext = ctx.getELContext();
	        if (elContext == null) {
	        	LOGGER.warning("Unable to get: " + ELContext.class.getName() + ". No items will be resolved for URI: " + uri);
	        	return null;
	        }

	        Application application = ctx.getApplication();
	        if (application == null) {
	        	LOGGER.warning("Unable to get: " + Application.class.getName() + ". No items will be resolved for URI: " + uri);
	        	return null;
	        }

	        ExpressionFactory expressionFactory = application.getExpressionFactory();
	        if (expressionFactory == null) {
	        	LOGGER.warning("Unable to get: " + ExpressionFactory.class.getName() + ". No items will be resolved for URI: " + uri);
	        	return null;
	        }

	        String expression = new StringBuilder(faces_exp_part1).append(xpath).append(faces_exp_part2).toString();

	        ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, expression, Map.class);
	        if (valueExpression == null) {
	        	LOGGER.warning("Unable to create value expression for '" + expression + "'. No items will be resolved for URI: " + uri);
	        	return null;
	        }

	        Object value = valueExpression.getValue(elContext);
	        if (value == null) {
	        	LOGGER.warning("No value was retrieved from the XPath: '" + xpath + "' and expression: '" + expression + "'");
	        	return null;
	        }

	        if (value instanceof Map) {
	        	@SuppressWarnings("unchecked")
				Map<Locale, Map<String, String>> items = (Map<Locale, Map<String, String>>) value;
	        	if (MapUtil.isEmpty(items)) {
	        		LOGGER.info("Returning null instead of empty map for XPath '" + xpath +	"') and expression ('" + expression + "')");
	        		return null;
	        	}

	        	return items;
	        } else {
	        	LOGGER.warning("Value of wrong type was retrieved from the XPath ('" + xpath +	"') and expression ('" + expression +
	        			"') value class: " + value.getClass().getName() + ". It must be: Map<Locale, Map<String, String>>. " +
	        			"Returning empty XML document");
	        	return null;
	        }
    	} catch (Exception e) {
    		String message = "Error resolving value for " + uri;
    		LOGGER.log(Level.WARNING, message, e);
    		CoreUtil.sendExceptionNotification(message, e);
    	}
    	return null;
    }
}