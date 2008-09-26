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
 * @version $Revision: 1.3 $
 * 
 *          Last modified: $Date: 2008/09/26 09:31:17 $ by $Author: arunas $
 * 
 */
public class SetErrorAction extends AbstractBoundAction {

	public SetErrorAction(Element element, Model model) {
		super(element, model);
	}

	public void init() throws XFormsException {
		super.init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chiba.xml.xforms.action.AbstractBoundAction#perform()
	 * 
	 * Before testing add instance, bind (in data_model of xforms)
	 * 
	 * <xf:bind id="errors" nodeset="instance('error-instance')/error"/>
	 * 
	 * <xf:instance id="error-instance" xmlns=""> <data> <error for=""/>
	 * </data> </xf:instance>
	 * 
	 *  
	 *  Add counter in xform and repeat output (in
	 * body area of xforms before all case):
	 * 
	 * <xf:output value=" if(count-non-empty(instance('error-instance')/error)!=0,concat('Your form has - ',count-non-empty(instance('error-instance')/error), ' ', if(count-non-empty(instance('error-instance')/error)=1, 'error', 'errors')), '')"/>
	 * 
	 * <xf:repeat bind="errors"> <xf:output ref="."/> </xf:repeat>
	 * 
	 * 
	 * Catcher and add namespace
	 * 
	 * <idega:setError ev:event="idega-validation-error"
	 * ref="instance('error-instance')/error"/>
	 * 
	 * submission action
	 * 
	 * <xf:action ev:event="DOMActivate" if="count-non-empty(instance('error-instance')/error)=0">
			<xf:send submission="submit_data_submission"/>
		</xf:action>
		
		
		component example
		
		<xf:group appearence="full">
			<xf:output class="alert" value="instance('error-instance')/error[@for='fbc_5']"/>
			<xf:input bind="bind.fbc_5" id="fbc_5">
				<xf:label model="data_model" ref="instance('localized_strings')/fbc_5.title[@lang=instance('localized_strings')/current_language]"/>
			</xf:input>
		</xf:group>
		
		error group 
		
		<xf:group appearance="full" relevant="if(count-non-empty(instance('error-instance')/error)!=0, 'true','false')">
			<xf:output value=" if(count-non-empty(instance('error-instance')/error)!=0,concat('Your form has - ',count-non-empty(instance('error-instance')/error), ' ', if(count-non-empty(instance('error-instance')/error)=1, 'error', 'errors')), '')"/>
			
			<xf:repeat bind="errors">
				<xf:output ref=".">
					<xf:label ref=""/>
				</xf:output>
			</xf:repeat>
		</xf:group>
		
		
	 */

	@SuppressWarnings("unchecked")
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

		Map<String, Object> errorCtx = (Map<String, Object>) eventContextInfo;

		String errorMsg = (String) errorCtx.get(ErrorMessageHandler.messageContextAtt);
		String targetAtt = (String) errorCtx.get(ErrorMessageHandler.targetContextAtt);

		System.out.println("______message=" + errorMsg + ", id=" + targetAtt);

		ModelItem errMi = instance.getModelItem(
				new StringBuilder(pathExpression).append("[@for='")
				.append(targetAtt).append("']").toString());
		if (errMi == null) {

			int contextSize = instance.countNodeset(pathExpression);
			if (contextSize == 0) {
				Logger.getLogger(getClass().getName()).log(Level.WARNING,
						"perform: nodeset '" + pathExpression
								+ "' is empty");
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