package com.idega.chiba.web.xml.xforms.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.xerces.dom.NodeImpl;
import org.chiba.xml.events.ChibaEventNames;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xforms.ui.BindingElement;
import org.chiba.xml.xforms.ui.UIElementState;
import org.chiba.xml.xforms.ui.state.BoundElementState;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.idega.chiba.web.xml.xforms.util.XFormsUtil;
/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.3 $
 *
 * Last modified: $Date: 2008/10/07 12:30:18 $ by $Author: arunas $
 *
 */

public class SwitchElement extends BindingElement{
	    private CaseElement selected = null;
	    private boolean initAfterReady = false;
	    public static String showAtt = "show";

	    /**
	     * Creates a new switch element handler.
	     *
	     * @param element the host document element.
	     * @param model the context model.
	     */
	    public SwitchElement(Element element, Model model) {
	        super(element, model);
	    }

	    // switch specific methods

	    /**
	     * Returns the currently selected Case.
	     *
	     * @return the currently selected Case.
	     */
	    public CaseElement getSelected() {
	        return this.selected;
	    }

	    /**
	     * Sets the currently selected Case.
	     *
	     * @param selected the the currently selected Case.
	     */
	    public void setSelected(CaseElement selected) {
	        this.selected = selected;
	    }

	    // lifecycle methods

	    /**
	     * Performs element init.
	     *
	     * @throws XFormsException if any error occurred during init.
	     */
	    public void init() throws XFormsException {
	        if (getLogger().isDebugEnabled()) {
	            getLogger().debug(this + " init");
	        }

	        initializeDefaultAction();
	        initializeInstanceNode();
	        initializeElementState();
	        initializeChildren();
	        initializeSwitch();
	    }

	    /**
	     * Performs element update.
	     *
	     * @throws XFormsException if any error occurred during update.
	     */
	    public void refresh() throws XFormsException {
	        if (getLogger().isDebugEnabled()) {
	            getLogger().debug(this + " update");
	        }

	        updateElementState();
	        updateChildren();
	        updateSwitch();
	    }

	    /**
	     * Performs element disposal.
	     *
	     * @throws XFormsException if any error occurred during disposal.
	     */
	    public void dispose() throws XFormsException {
	        if (getLogger().isDebugEnabled()) {
	            getLogger().debug(this + " dispose");
	        }

	        disposeDefaultAction();
	        disposeChildren();
	        disposeElementState();
	        disposeSwitch();
	        disposeSelf();
	    }

	    // lifecycle template methods

	    /**
	     * Initializes the Case elements.
	     * <p/>
	     * If multiple Cases within a Switch are selected, the first selected Case
	     * remains and all others are deselected. If none are selected, the first
	     * becomes selected.
	     */
	    protected final void initializeSwitch() throws XFormsException {
	    	
	        NodeList childNodes = getElement().getChildNodes();
	        List<CaseElement> cases = new ArrayList<CaseElement>(childNodes.getLength());
	        List<Integer> selectedList = new ArrayList<Integer>(childNodes.getLength());
	        
	        Node node;
	        CaseElement caseElement;
	        String showAttribute;
	        Boolean showValue;
	        
	        int selection = -1;
	       
	        for (int index = 0; index < childNodes.getLength(); index++) {
	            node = childNodes.item(index);
	            
	            if (node.getNodeType() == Node.ELEMENT_NODE && CASE.equals(node.getLocalName())) {
	            	
	                caseElement = (CaseElement) ((NodeImpl) node).getUserData();
	                cases.add(caseElement);
	                            
	                showAttribute = caseElement.getXFormsAttribute(showAtt);
	                
	                if (showAttribute != null) {
	                	
	                	 showValue = (Boolean)XFormsUtil.getValueFromExpression(showAttribute, this);
	                	 
	                	 if (showValue) {
	 	                    // keep *first* selected case position
	 	                    selection = cases.size() - 1;
	 	                    selectedList.add(selection);
	 	                }
	                }
	                
	            }
	        }

	        if (selection == -1) {
	            if (getLogger().isDebugEnabled()) {
	                getLogger().debug(this + " init: choosing first case for selection by default");
	            }

	            // select first case if none is selected
                selection = 0;

	        }

	        // perform selection/deselection
	        for (int index = 0; index < cases.size(); index++) {
	            caseElement = (CaseElement) cases.get(index);
	            if (!selectedList.isEmpty() && index < selectedList.size()) {
	            	selection = selectedList.get(index);
	            }
	            
	            if (index == selection) {
	                if (getLogger().isDebugEnabled()) {
	                    getLogger().debug(this + " init: selecting case '" + caseElement.getId() + "'");
	                }
	                caseElement.select();
	            }
	            else {
	                if (getLogger().isDebugEnabled()) {
	                    getLogger().debug(this + " init: deselecting case '" + caseElement.getId() + "'");
	                }
	                caseElement.deselect();
	            }
	        }

	        // set state for updateSwitch()
	        this.initAfterReady = this.model.isReady();
	    }

	    /**
	     * Updates the Switch element.
	     */
	    protected final void updateSwitch() throws XFormsException {
	        // if init happened after xforms-ready we are are part of repeat insert
	        // processing so we have to dispatch an event to propagate the initial
	        // selection state
	        if (this.initAfterReady) {
	            // dispatch internal chiba event
	            HashMap<String, String> map = new HashMap<String, String>();
	            map.put("selected", this.selected.getId());
	            this.container.dispatch(this.target, ChibaEventNames.SWITCH_TOGGLED, map);

	            // reset state to prevent event being dispatched more than once
	            this.initAfterReady = false;
	        }
	    }

	    /**
	     * Disposes the Switch element.
	     */
	    protected final void disposeSwitch() {
	        this.selected = null;
	    }

	    // template methods

	    /**
	     * Factory method for the element state.
	     *
	     * @return an element state implementation or <code>null</code> if no state
	     *         keeping is required.
	     * @throws XFormsException if an error occurred during creation.
	     */
	    protected UIElementState createElementState() throws XFormsException {
	        return isBound() ? new BoundElementState(false, false) : null;
	    }


}
