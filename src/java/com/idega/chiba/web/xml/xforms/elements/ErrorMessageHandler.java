package com.idega.chiba.web.xml.xforms.elements;

import org.chiba.xml.xforms.Container;
import org.chiba.xml.xforms.core.ModelItem;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.6 $
 * 
 *          Last modified: $Date: 2008/10/07 13:10:11 $ by $Author: civilis $
 * 
 */
public interface ErrorMessageHandler {

	public static final String validationErrorType = "idega-validation-error";
	public static final String messageContextAtt = "message";
	public static final String targetContextAtt = "target";

	public abstract void send(ModelItem mi, Container container,
			String targetId, String componentId, String message);
}