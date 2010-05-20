package com.idega.chiba.web.xml.xforms.connector.context;

import org.chiba.xml.xforms.connector.URIResolver;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;

import com.idega.chiba.web.xml.xforms.connector.context.beans.ChoiceListData;
import com.idega.chiba.web.xml.xforms.connector.context.beans.LocalizedEntries;
import com.idega.util.text.Item;
import com.idega.util.xml.XmlUtil;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

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
    @SuppressWarnings("unchecked")
	@Override
	public Object resolve() throws XFormsException {
        try {
        	String xpath = new URI(getURI()).getSchemeSpecificPart();
            FacesContext ctx = FacesContext.getCurrentInstance();
            
            Object value = ctx.getApplication().getExpressionFactory().createValueExpression(ctx.getELContext(),
            		new StringBuilder(faces_exp_part1).append(xpath).append(faces_exp_part2).toString(), Object.class).getValue(ctx.getELContext());
            
            if (value == null) {
            	LOGGER.warning("No value was retrieved from the key: " + xpath);
            	return XmlUtil.getDocumentBuilder().newDocument();
            }
            	
            if (!(value instanceof Map)) {
            	LOGGER.warning("Value of wrong type was retrieved from the key: " + xpath +	" value class: " + value.getClass().getName() +
            			". It must be: Map<Locale, Map<String, String>>. Returning empty XML document");
            	return XmlUtil.getDocumentBuilder().newDocument();
            }

            Map<Locale, Map<String, String>> localizedItems = (Map<Locale, Map<String, String>>) value;
	        return createResponseDocument(localizedItems);
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
}