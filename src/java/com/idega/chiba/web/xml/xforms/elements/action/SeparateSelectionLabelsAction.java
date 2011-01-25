package com.idega.chiba.web.xml.xforms.elements.action;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.xerces.dom.ElementNSImpl;
import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.xforms.Container;
import org.chiba.xml.xforms.action.AbstractBoundAction;
import org.chiba.xml.xforms.core.BindingResolver;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.idega.chiba.web.xml.xforms.functions.ExtensionFunctionUtil;
import com.idega.chiba.web.xml.xforms.util.XFormsUtil;
import com.idega.util.CoreConstants;
import com.idega.util.FilePathBuilder;
import com.idega.util.StringUtil;

public class SeparateSelectionLabelsAction extends AbstractBoundAction {
	
	private String itemsNodesetExp;
	private String ref;
	private String val;
	private static final String separator = ", ";
	
	public SeparateSelectionLabelsAction(Element element, Model model) {
		super(element, model);
	}
	
	@Override
	public void init() throws XFormsException {
		super.init();
		
		itemsNodesetExp = getXFormsAttribute("itemsNodeset");
		ref = getXFormsAttribute("ref");
		val = getXFormsAttribute("val");
		
		if (StringUtil.isEmpty(itemsNodesetExp)) {
			throw new IllegalStateException(
			        "No itemsNodeset attribute provided");
		}
		
		if (itemsNodesetExp.endsWith(CoreConstants.SLASH))
			throw new IllegalStateException(
			        "No trailing slashes permitted in the items nodeset expression. Expression resolved: "
			                + itemsNodesetExp);
		
		if (StringUtil.isEmpty(ref)) {
			throw new IllegalStateException("No ref attribute provided");
		}
		
		if (StringUtil.isEmpty(val)) {
			throw new IllegalStateException("No val attribute provided");
		}
	}
	
	@Override
	public void perform() throws XFormsException {
		super.perform();
		
		final Iterator<Pointer> it = getItemsIterator();
		final StringBuilder separatedLabels = new StringBuilder();
		
		final List<String> vals = getVals();
		
		boolean first = true;
		
		while (it.hasNext()) {
			final Element itemElement = (Element) it.next().getNode();
			
			if (itemElement != null) {
				
				final Element labelElement = DOMUtil.getChildElement(
				    itemElement, "itemLabel");
				final Element valueElement = DOMUtil.getChildElement(
				    itemElement, "itemValue");
				
				if (labelElement != null && valueElement != null
				        && vals.contains(valueElement.getTextContent())
				        && !StringUtil.isEmpty(labelElement.getTextContent())) {
					
					if (!first)
						separatedLabels.append(separator);
					
					separatedLabels.append(labelElement.getTextContent());
					first = false;
				}
				
			}
		}
		
		setLabels(separatedLabels.toString());
	}
	
	private Instance getInstance() {
		return this.model.getInstance(getInstanceId());
	}
	
	private void setLabels(String labels) {
		
		try {
			getInstance().setNodeValue(getRef(), labels);
			
		} catch (XFormsException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Iterator<Pointer> getItemsIterator() {
		return getInstance().getPointerIterator(getItemsExpression());
	}
	
	private List<String> getVals() {
		return Arrays.asList(((String) getInstance().getPointer(getVal())
		        .getValue()).split(CoreConstants.SPACE));
	}
	
	private String getItemsExpression() {
		
		return itemsNodesetExp + "//item";
	}
	
	private String getRef() {
		return ref;
	}
	
	private String getVal() {
		return val;
	}
}