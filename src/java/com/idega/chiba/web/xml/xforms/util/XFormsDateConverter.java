package com.idega.chiba.web.xml.xforms.util;

import java.text.ParseException;
import java.util.Date;

public interface XFormsDateConverter {

	public String convertDateToComplyWithXForms(Date date);
	public Date convertStringFromXFormsToDate(String dateStr) throws ParseException;
	
}