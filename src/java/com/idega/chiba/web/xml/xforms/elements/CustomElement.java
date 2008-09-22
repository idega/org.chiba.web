package com.idega.chiba.web.xml.xforms.elements;

import org.chiba.xml.xforms.XFormsElement;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;

public class CustomElement extends XFormsElement{
//    protected static Log LOGGER = LogFactory.getLog(CustomComponent.class);

	public CustomElement(Element element, Model model) {
		super(element, model);
	}


	@Override
	public void dispose() throws XFormsException {
		   if (getLogger().isDebugEnabled()) {
	            getLogger().debug(this + " dispose");
	        }
		System.out.println("testing");
	}

	@Override
	public void init() throws XFormsException {
		   if (getLogger().isDebugEnabled()) {
	            getLogger().debug(this + " init");
	        }
		
	}


	
	

}
