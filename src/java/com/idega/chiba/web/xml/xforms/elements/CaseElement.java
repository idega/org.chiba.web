package com.idega.chiba.web.xml.xforms.elements;

import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xforms.ui.AbstractUIElement;
import org.chiba.xml.xforms.ui.state.UIElementStateUtil;
import org.w3c.dom.Element;
/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/10/02 08:52:33 $ by $Author: arunas $
 *
 */
public class CaseElement extends AbstractUIElement{

	    public CaseElement(Element element, Model model)
	    {
	        super(element, model);
	    }

	    public boolean isSelected()
	    {
	        return selected;
	    }

	    public SwitchElement getSwitch()
	    {
	        return (SwitchElement)getParentObject();
	    }

	    public void select()
	        throws XFormsException
	    {
	        selected = true;
	        UIElementStateUtil.setStateAttribute(state, "selected", String.valueOf(selected));
	        getSwitch().setSelected(this);
	    }

	    public void deselect()
	    {
	        selected = false;
	        UIElementStateUtil.setStateAttribute(state, "selected", String.valueOf(selected));
	    }

	    @Override
		public void init() throws XFormsException {
	        if (getLogger().isDebugEnabled())
	            getLogger().debug((new StringBuilder()).append(this).append(" init").toString());

	        initializeCase();
	        initializeChildren();
	        initializeActions();
	    }

	    @Override
		public void dispose()
	        throws XFormsException
	    {
	        if(getLogger().isDebugEnabled())
	            getLogger().debug((new StringBuilder()).append(this).append(" dispose").toString());
	        disposeCase();
	        disposeChildren();
	        disposeSelf();
	    }

	    protected final void initializeCase()
	    {
	        state = UIElementStateUtil.createStateElement(element);
	    }

	    @Override
		protected void updateChildren()
	        throws XFormsException
	    {
	        if(isSelected())
	            super.updateChildren();
	    }

	    protected final void disposeCase()
	    {
	        element.removeChild(state);
	    }

	    private boolean selected;
	    private Element state;



}
