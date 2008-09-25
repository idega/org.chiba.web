package com.idega.chiba.web.xml.xforms.elements;

import java.util.HashMap;
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
 * @version $Revision: 1.4 $
 * 
 *          Last modified: $Date: 2008/09/25 18:09:46 $ by $Author: civilis $
 * 
 */
@Scope("singleton")
@Service
public class ErrorMessageHandlerImpl implements ErrorMessageHandler {
	
	public void send(ModelItem mi, Container container, EventTarget target, String componentId, String message) {
		
		HashMap<String, Object> map = new HashMap<String, Object>(2);
		map.put(messageContextAtt, message);
		map.put(targetContextAtt, componentId);
		
		try {
			container.dispatch(target, validationErrorType, map);

		} catch (XFormsException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "Exception while dispatching "+validationErrorType+" event", e);
		}
	}
}