package com.idega.chiba.web.xml.xforms.connector.context.beans;

import java.util.ArrayList;
import java.util.List;

import com.idega.util.text.Item;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/11/26 08:49:40 $ by $Author: arunas $
 */
public class LocalizedEntries {
	
	private List<Item> items = new ArrayList<Item>();
	private String lang;
	
	public List<Item> getItems() {
		return items;
	}
	
	public void add(Item item) {
		items.add(item);
	}
	
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
}