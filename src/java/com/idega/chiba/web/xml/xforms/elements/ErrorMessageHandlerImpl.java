package com.idega.chiba.web.xml.xforms.elements;

import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.chiba.xml.xforms.Container;
import org.chiba.xml.xforms.core.ModelItem;
import org.chiba.xml.xforms.exception.XFormsException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.events.EventTarget;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 * 
 *          Last modified: $Date: 2008/09/24 13:46:12 $ by $Author: civilis $
 * 
 */
@Scope("singleton")
@Service
public class ErrorMessageHandlerImpl implements ErrorMessageHandler {
	
	public void send(ModelItem mi, Container container, EventTarget target, String componentId, String message) {
		
		System.out.println("WILL SEND MESSAGE ERROR="+message);

		HashMap<String, Object> map = new HashMap<String, Object>(2);
		map.put(messageContextAtt, message);
		map.put(targetContextAtt, componentId);

		try {
			System.out.println("_______DISPATHCING EVENT");
			container.dispatch(target, validationErrorType, map);

		} catch (XFormsException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Exception while dispatching "+validationErrorType+" event", e);
		}
	}
	
	public String getDefaultErrorMessage(Locale locale, ErrorType errType) {
		
		final String message;
	
		switch (errType) {
		case required:
			message = "Required field is empty";
			break;
			
		case validation:
			message = "Value provided is not valid";
			break;
			
		case constraint:
			message = "The constraint was not met";
			break;
			
		default:
			message = "Incorrect field";
			break;
		}
		return message;
	}
}