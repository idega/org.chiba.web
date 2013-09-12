package com.idega.chiba.web.xml.xforms.ui;

import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xforms.ui.AbstractFormControl;
import org.w3c.dom.Element;

import com.idega.chiba.web.xml.xforms.bean.XformChangeBean;
import com.idega.util.expression.ELUtil;

public class XformChangeBeanDeclaration extends AbstractFormControl{

	public XformChangeBeanDeclaration(Element element, Model model) {
		super(element, model);
	}


	public void init() throws XFormsException{
		String value = getXFormsAttribute("value");
		XformChangeBean xformChangeBean =  ELUtil.getInstance().getBean(XformChangeBean.BEAN_NAME);
		xformChangeBean.addXformChangeBean(value);
	}
	@Override
	public void setValue(String paramString) throws XFormsException {
		return;
	}
	
}
