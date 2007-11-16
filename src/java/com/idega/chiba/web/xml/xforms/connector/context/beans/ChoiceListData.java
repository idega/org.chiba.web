package com.idega.chiba.web.xml.xforms.connector.context.beans;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import com.idega.util.xml.XmlUtil;
import com.thoughtworks.xstream.XStream;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2007/11/16 13:53:11 $ by $Author: civilis $
 */
public class ChoiceListData {
    
	private static final String choiceListDataElementName = "choiceListData";
	private static final String localizedEntriesElementName = "localizedEntries";
	private static final String itemElementName = "item";
	private static final String langAttributeName = "lang";
	private static final String itemsImplicitCollectionName = "items";
	private static final String localizedEntriesImplicitCollectionName = "localizedEntries";
	
	private List<LocalizedEntries> localizedEntries = new ArrayList<LocalizedEntries>();

	public List<LocalizedEntries> getLocalizedEntries() {
		return localizedEntries;
	}

	public void add(LocalizedEntries localizedEntries) {
		this.localizedEntries.add(localizedEntries);
	}
	
	public Document getDocument() throws Exception {

		XStream xstream = new XStream();
    	xstream.alias(choiceListDataElementName, ChoiceListData.class);
    	xstream.alias(localizedEntriesElementName, LocalizedEntries.class);
    	xstream.alias(itemElementName, Item.class);
    	
    	xstream.useAttributeFor(LocalizedEntries.class, langAttributeName);
    	
    	xstream.addImplicitCollection(LocalizedEntries.class, itemsImplicitCollectionName);
    	xstream.addImplicitCollection(ChoiceListData.class, localizedEntriesImplicitCollectionName);
    	
    	ByteArrayOutputStream output = new ByteArrayOutputStream();
    	xstream.toXML(this, output);
    	
    	return XmlUtil.getDocumentBuilder().parse(new ByteArrayInputStream(output.toByteArray()));
	}
}