package com.idega.chiba.web.xml.xforms.elements.action;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.chiba.xml.xforms.action.AbstractBoundAction;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.core.ModelItem;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;

import com.idega.chiba.web.xml.xforms.elements.ErrorMessageHandler;
import com.idega.util.CoreConstants;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.7 $
 * 
 *          Last modified: $Date: 2008/09/30 20:25:34 $ by $Author: civilis $
 * 
 */
public class SetErrorAction extends AbstractBoundAction {

	public SetErrorAction(Element element, Model model) {
		super(element, model);
	}

	public void init() throws XFormsException {
		super.init();
	}

	public void perform() throws XFormsException {
		super.perform();

		Model dataModel = getContainerObject().getModel("data_model");
		Instance instance = dataModel.getInstance("error-instance");

		String pathExpression = getLocationPath();

		if (!instance.existsNode(pathExpression)) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING,
					"perform: nodeset '" + pathExpression + "' is empty");
			return;
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> errorCtx = (Map<String, Object>) eventContextInfo;

		String errorMsg = (String) errorCtx
				.get(ErrorMessageHandler.messageContextAtt);
		String targetAtt = (String) errorCtx
				.get(ErrorMessageHandler.targetContextAtt);

//		find existing error for the target id
		ModelItem errMi = instance.getModelItem(new StringBuilder(
				pathExpression).append("[@for='").append(targetAtt)
				.append("']").toString());
		
		if (errMi == null) {
			
//			if not found, creating new error (copying from template, @see InsertAction)

			int contextSize = instance.countNodeset(pathExpression);
			if (contextSize == 0) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING,
						"perform: nodeset '" + pathExpression + "' is empty");
				return;
			}

			String origin = new StringBuffer(pathExpression).append('[')
					.append(contextSize).append(']').toString();
			String after = new StringBuffer(pathExpression).append('[').append(
					contextSize + 1).append(']').toString();

			instance.insertNode(origin, after);

			errMi = instance.getModelItem(after);

			Element el = (Element) errMi.getNode();
			el.setAttribute("for", targetAtt);
		}

		errMi.setValue(errorMsg != null ? errorMsg : CoreConstants.EMPTY);

		doRebuild(true);
		doRecalculate(true);
		doRevalidate(true);
		doRefresh(true);
	}
}