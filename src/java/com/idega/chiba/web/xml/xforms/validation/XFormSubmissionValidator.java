package com.idega.chiba.web.xml.xforms.validation;

public interface XFormSubmissionValidator {

	public abstract boolean isRequiredToBeLoggedIn(String uri);
	
}