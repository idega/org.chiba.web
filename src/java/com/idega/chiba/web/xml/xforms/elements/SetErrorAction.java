package com.idega.chiba.web.xml.xforms.elements;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.NodeImpl;
import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.xforms.action.AbstractBoundAction;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.core.ModelItem;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.idega.util.xml.XPathUtil;

public class SetErrorAction extends AbstractBoundAction {

	protected static Log LOGGER = LogFactory.getLog(SetErrorAction.class);
	private XPathUtil errorNodeXPUT;

	public SetErrorAction(Element element, Model model) {
		super(element, model);
		// TODO Auto-generated constructor stub
	}

	public void init() throws XFormsException {
		super.init();

	}

	@SuppressWarnings("unchecked")
	public void perform() throws XFormsException {
		super.perform();

		System.out.println("context:");
		System.out.println(eventContextInfo);

		Instance instance = this.model.getInstance(getInstanceId());
		Element instanceElem = instance.getElement();

		String pathExpression = getLocationPath();

		if (!instance.existsNode(pathExpression)) {
			getLogger().warn(this + " perform: nodeset '" + pathExpression + "' is empty");
			return;
		}

		int contextSize = instance.countNodeset(pathExpression);
		String origin;
		Element nodeErrorElem;

		if (eventContextInfo instanceof Map) {

			Map<String, Object> errorMsgs = (Map<String, Object>) eventContextInfo;

			String errorMsg = errorMsgs.get(ErrorMessageHandler.messageContextAtt).toString();
			String targetAtt = errorMsgs.get(ErrorMessageHandler.targetContextAtt).toString();

			System.out.println("messages : " + errorMsg + "  " + "id " + targetAtt);

			errorNodeXPUT = new XPathUtil(".//error[@id='" + targetAtt + "'][@type='test']");

			nodeErrorElem = errorNodeXPUT.getNode(instanceElem);

			if (nodeErrorElem != null) {

				origin = new StringBuffer(pathExpression).append("[@id='").append(targetAtt).append("']").append("[@type='").append("test").append("']").toString();

				instance.setNodeValue(origin, errorMsg != null ? errorMsg : "");

			} else {

				errorNodeXPUT = new XPathUtil(".//error[@id=''][@type='']");

				nodeErrorElem = errorNodeXPUT.getNode(instanceElem);

				nodeErrorElem.setTextContent(errorMsg);
				nodeErrorElem.setAttribute("type", "test");
				nodeErrorElem.setAttribute("id", targetAtt);

				DOMUtil.prettyPrintDOM(nodeErrorElem);

				origin = new StringBuffer(pathExpression).append('[').append(contextSize).append(']').toString();

			
				Document instanceDoc = instance.getInstanceDocument();
				 
				Node imported	= instanceDoc.importNode((Node)nodeErrorElem, true);
				
				DOMUtil.prettyPrintDOM(instanceDoc);
					
				ModelItem item = instance.getModelItem(pathExpression);
					
				((Element)item.getNode()).appendChild(imported);
					  
			    model.addChanged((NodeImpl)item.getNode());

			

			}
		
		}

		doRecalculate(true);
		doRevalidate(true);
		doRefresh(true);

	}


}
