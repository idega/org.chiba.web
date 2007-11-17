package com.idega.chiba.web.xml.xforms.connector.context.beans;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2007/11/17 11:19:08 $ by $Author: civilis $
 */
public class Item {
	 
	private String itemLabel;
	private String itemValue;
	
	public Item() {	}
	
	public Item(String value, String label) {
		setItemValue(value);
		setItemLabel(label);
	}
	
	public String getItemLabel() {
		return itemLabel;
	}
	public void setItemLabel(String itemLabel) {
		this.itemLabel = itemLabel;
	}
	public String getItemValue() {
		return itemValue;
	}
	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}
}