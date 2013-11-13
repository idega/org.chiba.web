package com.idega.chiba.web.xml.xforms.bean;

import java.util.ArrayList;
import java.util.List;

import org.chiba.xml.xforms.ui.XformDocumentChange;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.util.expression.ELUtil;


@Service(XformChangeBean.BEAN_NAME)
@Scope("request")
public class XformChangeBean {
	public static final String BEAN_NAME = "xformChangeBean";
	
	private List<String> xformChangeBeansNames;

	public List<XformDocumentChange> getXformDocumentChangeBeans() {
		List<String> xformChangeBeansNames = getXformChangeBeansNames();
		ELUtil elUtil = ELUtil.getInstance();
		List<XformDocumentChange> xformDocumentChangeBeans = new ArrayList<XformDocumentChange>(xformChangeBeansNames.size());
		for(String name : xformChangeBeansNames){
			XformDocumentChange bean = elUtil.getBean(name);
			if(bean == null){
				continue;
			}
			xformDocumentChangeBeans.add(bean);
		}
		return xformDocumentChangeBeans;
	}

	public void addXformChangeBean(String xformChangeBeanName) {
		getXformChangeBeansNames().add(xformChangeBeanName);
	}

	private List<String> getXformChangeBeansNames() {
		if(xformChangeBeansNames == null){
			xformChangeBeansNames = new ArrayList<String>();
		}
		return xformChangeBeansNames;
	}

}
