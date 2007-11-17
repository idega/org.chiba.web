package com.idega.chiba.web.xml.xforms.connector.context;

import org.chiba.xml.xforms.connector.URIResolver;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;

import com.idega.chiba.web.xml.xforms.connector.context.beans.ChoiceListData;
import com.idega.chiba.web.xml.xforms.connector.context.beans.LocalizedEntries;
import com.idega.chiba.web.xml.xforms.connector.context.beans.Item;
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
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2007/11/16 13:53:11 $ by $Author: civilis $
 */
public class ContextXmlResolver extends org.chiba.xml.xforms.connector.context.ContextResolver implements URIResolver {

	private static final String faces_exp_part1 = "#{";
	private static final String faces_exp_part2 = "}";
	
	/**
	 * resolves object, which is configured in the faces-config.xml, method value
	 */
    public Object resolve() throws XFormsException {
    	
        try {
        	String xpath = new URI(getURI()).getSchemeSpecificPart();
            FacesContext ctx = FacesContext.getCurrentInstance();
            
            Object value = 
            	ctx.getApplication().createValueBinding(
            		new StringBuilder(faces_exp_part1)
            		.append(xpath)
            		.append(faces_exp_part2)
            		.toString()
            	).getValue(ctx);
            
            if(value == null) {
            	
            	Logger.getLogger(ContextXmlResolver.class.getName()).log(Level.WARNING, "No value was retrieved from the key: "+xpath);
            	return XmlUtil.getDocumentBuilder().newDocument();
            }
            	
            if(!(value instanceof Map)) {
            	
            	Logger.getLogger(ContextXmlResolver.class.getName()).log(Level.WARNING, "Value of wrong type was retrieved from the key: "+xpath+" value class: "+value.getClass().getName());
            	return XmlUtil.getDocumentBuilder().newDocument();
            }

            @SuppressWarnings("unchecked")
            Map<Locale, Map<String, String>> localizedItems = (Map<Locale, Map<String, String>>)value;
            
	        return createResponseDocument(localizedItems);
        } catch (Exception e) {
        	
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
			localizedEntries.setLang(localizedItemsEntry.getKey().getLanguage());
			
			for (Entry<String, String> itemEntry : localizedItemsEntry.getValue().entrySet()) {

				localizedEntries.add(new Item(itemEntry.getKey(), itemEntry.getValue()));
			}
			
			choiceListData.add(localizedEntries);
		}
    	
        return choiceListData.getDocument();
	}
}