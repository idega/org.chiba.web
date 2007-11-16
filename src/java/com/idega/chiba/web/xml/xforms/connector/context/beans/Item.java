package com.idega.chiba.web.xml.xforms.connector.context.beans;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2007/11/16 13:53:11 $ by $Author: civilis $
 */
public class Item {
	 
	private String itemLabel;
	private String itemValue;
	
	public Item() {	}
	
	public Item(String label, String value) {
		setItemLabel(label);
		setItemValue(value);
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