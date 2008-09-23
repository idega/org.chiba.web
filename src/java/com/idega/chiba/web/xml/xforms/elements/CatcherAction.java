package com.idega.chiba.web.xml.xforms.elements;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chiba.xml.xforms.action.AbstractAction;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;

public class CatcherAction  extends AbstractAction{
	
	protected static Log LOGGER = LogFactory.getLog(CatcherAction.class);
    

	public CatcherAction(Element element, Model model) {
		super(element, model);
		// TODO Auto-generated constructor stub
	}
	
    public void init() throws XFormsException {
        super.init();
     
    }        

	public void perform() throws XFormsException {
		
	}

}
