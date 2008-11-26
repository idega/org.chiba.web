package com.idega.chiba.web.xml.xforms.elements.action;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chiba.xml.events.ChibaEventNames;
import org.chiba.xml.events.XFormsEventNames;
import org.chiba.xml.xforms.action.AbstractAction;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsBindingException;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;

import com.idega.chiba.web.xml.xforms.elements.CaseElement;
import com.idega.chiba.web.xml.xforms.elements.SwitchElement;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/11/26 08:49:40 $ by $Author: arunas $
 *
 */
public class ToggleActionXF  extends AbstractAction {
	   protected static Log LOGGER = LogFactory.getLog(ToggleActionXF.class);
	    private String caseAttribute = null;

	    /**
	     * Creates a toggle action implementation.
	     *
	     * @param element the element.
	     * @param model the context model.
	     */
	    public ToggleActionXF(Element element, Model model) {
	        super(element, model);
	    }

	    // lifecycle methods

	    /**
	     * Performs element init.
	     *
	     * @throws XFormsException if any error occurred during init.
	     */
	    public void init() throws XFormsException {
	        super.init();

	        this.caseAttribute = getXFormsAttribute(CASE_ATTRIBUTE);
	        if (this.caseAttribute == null) {
	            throw new XFormsBindingException("missing case attribute at " + this, this.target, null);
	        }
	    }

	    // implementation of 'org.chiba.xml.xforms.action.XFormsAction'

	    /**
	     * Performs the <code>toggle</code> action.
	     *
	     * @throws XFormsException if an error occurred during <code>toggle</code>
	     * processing.
	     */
	    public void perform() throws XFormsException {
	        // check case idref
	        Object caseObject = this.container.lookup(this.caseAttribute);
	        if (caseObject == null || !(caseObject instanceof CaseElement)) {
	            throw new XFormsBindingException("invalid case id at " + this, this.target, this.caseAttribute);
	        }

	        // obtain case and switch elements
	        CaseElement toSelect = (CaseElement) caseObject;
	        SwitchElement switchElement = toSelect.getSwitch();
	        CaseElement toDeselect = switchElement.getSelected();

	        // perform selection/deselection
	        toDeselect.deselect();
	        toSelect.select();

	        // dispatch xforms-deselect and xforms-select events
	        this.container.dispatch(toDeselect.getTarget(), XFormsEventNames.DESELECT, null);
	        this.container.dispatch(toSelect.getTarget(), XFormsEventNames.SELECT, null);

	        // dispatch internal chiba event
	        HashMap<String, String> map = new HashMap<String, String>();
	        map.put("selected", toSelect.getId());
	        map.put("deselected", toDeselect.getId());
	        this.container.dispatch(switchElement.getTarget(), ChibaEventNames.SWITCH_TOGGLED, map);

	        //as we did an optimization to only update selected 'case' elements we need to refresh after a toggle
	        doRefresh(true);
	    }

}
