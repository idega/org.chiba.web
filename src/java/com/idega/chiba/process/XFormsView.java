package com.idega.chiba.process;

import com.idega.jbpm.def.impl.DefaultViewImpl;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2007/09/23 06:58:25 $ by $Author: civilis $
 */
public class XFormsView extends DefaultViewImpl {

	public static final String VIEW_TYPE = "xforms";
	
	public XFormsView() {
		setViewType(VIEW_TYPE);
	}
}