package com.idega.chiba.web.xml.xforms.elements;

import java.util.Locale;

import org.chiba.xml.xforms.Container;
import org.chiba.xml.xforms.core.ModelItem;
import org.w3c.dom.events.EventTarget;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/09/24 13:46:18 $ by $Author: civilis $
 *
 */
public interface ErrorMessageHandler {

	public static final String validationErrorType = "idega-validation-error";
	public static final String messageContextAtt = "message";
	public static final String targetContextAtt = "target";
	
	enum ErrorType {
    	required,
    	validation,
    	constraint,
    	custom
    }
	
	public abstract void send(ModelItem mi, Container container, EventTarget target, String componentId, String message);
	
	public abstract String getDefaultErrorMessage(Locale locale, ErrorType errType);
}