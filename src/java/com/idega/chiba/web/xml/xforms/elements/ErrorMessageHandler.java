package com.idega.chiba.web.xml.xforms.elements;

import java.util.Locale;

import org.chiba.xml.xforms.Container;
import org.chiba.xml.xforms.core.ModelItem;
import org.w3c.dom.events.EventTarget;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/09/25 09:23:46 $ by $Author: civilis $
 *
 */
public interface ErrorMessageHandler {

	public static final String validationErrorType = "idega-validation-error";
	public static final String messageContextAtt = "message";
	public static final String targetContextAtt = "target";
	public static final String errorTypeContextAtt = "errType";
	
	public enum ErrorType {
    	required,
    	validation,
    	constraint,
    	custom
    }
	
	public abstract void send(ModelItem mi, Container container, EventTarget target, String componentId, String message, ErrorType errType);
	
	public abstract String getDefaultErrorMessage(Locale locale, ErrorType errType);
}