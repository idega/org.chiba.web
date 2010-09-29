package com.idega.chiba.web.xml.xforms.elements.action;

import java.util.Iterator;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.xerces.dom.NodeImpl;
import org.chiba.xml.xforms.action.SetValueAction;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.core.ModelItem;
import org.chiba.xml.xforms.exception.XFormsComputeException;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.idega.util.CoreConstants;

public class IdegaSetValueAction extends SetValueAction {
	
	private static final String INSERT_ATTRIBUTE = "insert";
	private static final String MULTIPLE_ATTRIBUTE = "multiple";
	
	private String nodeValue;
	private String valueAttribute;
	private String insertAttribute;
	private String multipleAttribute;
	
	private String locationPath;
	private Instance instance;
	
	public IdegaSetValueAction(Element element, Model model) {
		super(element, model);
	}
	
	/**
	 * Performs the <code>setvalue</code> action.
	 * 
	 * @throws XFormsException
	 *             if an error occurred during <code>setvalue</code> processing.
	 */
	@Override
	public void init() throws XFormsException {
		super.init();
		
		this.valueAttribute = getXFormsAttribute(VALUE_ATTRIBUTE);
		
		setInsertAttribute(getXFormsAttribute(INSERT_ATTRIBUTE));
		setMultipleAttribute(getXFormsAttribute(MULTIPLE_ATTRIBUTE));
		
		if (this.valueAttribute == null) {
			org.w3c.dom.Node child = this.element.getFirstChild();
			
			if ((child != null) && (child.getNodeType() == Node.TEXT_NODE)) {
				this.nodeValue = child.getNodeValue();
			} else {
				this.nodeValue = CoreConstants.EMPTY;
			}
		}
	}
	
	private boolean isNodeExists() {
		String locationPath = getLocationPath();
		Instance instance = getInstance();
		if (!instance.existsNode(locationPath)) {
			getLogger().warn(this + " perform: nodeset '" + locationPath + "' is empty");
			return false;
		} else
			return true;
	}
	
	@Override
	public void perform() throws XFormsException {
		if (isNodeExists()) {
			String valueAttribute = getValueAttribute();
			if (valueAttribute != null) {
				Object value = getValue();
				
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(this + " perform: setting evaluated value '" + value + "'");
				}
				
				setValue(value);
			} else {
				String nodeValue = getNodeValue();
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(this + " perform: setting literal value '" + nodeValue + "'");
				}
				
				setValue(nodeValue);
			}
			updateBehavior();
		}
	}
	
	protected void updateBehavior() throws XFormsException {
		doRecalculate(true);
		doRevalidate(true);
		doRefresh(true);
	}
	
	protected Object getValue() throws XFormsException {
		// since jxpath doesn't provide a means for evaluating an expression
		// in a certain context, we use a trick here: the expression will be
		// evaluated during getPointer and the result stored as a variable
		JXPathContext context = instance.getInstanceContext();
		
		String currentPath = getParentContextPath(this.element);
		context.getVariables().declareVariable("currentContextPath", currentPath);
		try {
			String locationPath = getLocationPath();
			context.getPointer(locationPath + "[chiba:declare('node-value', " + valueAttribute + ")]");
		} catch (Exception e) {
			throw new XFormsComputeException("invalid value expression at " + this, e, this.target, valueAttribute);
		}
		
		Object value = context.getValue("chiba:undeclare('node-value')");
		
		// check for string conversion to prevent something like "5 + 0" to be evaluated to "5.0"
		if (value instanceof Double) {
			// Additionally check for special cases
			double doubleValue = ((Double) value).doubleValue();
			if (!(Double.isNaN(doubleValue) || Double.isInfinite(doubleValue))) {
				value = context.getValue("string(" + value + ")");
			}
		}
		context.getVariables().undeclareVariable("currentContextPath");
		
		return value;
	}
	
	protected void setValue(Object value) throws XFormsException {
		if (value instanceof Document) {
			setDocumentTypeValue((Document) value);
		} else {
			setObjectTypeValue(value);
		}
	}
	
	protected void setDocumentTypeValue(Document itemListDoc) throws XFormsException {
		Element itemsElem = itemListDoc.getDocumentElement();
		if (isInserting())
			insertItems(itemsElem.getChildNodes());
		else
			overrideExistingItems(itemsElem.getChildNodes());
	}
	
	protected void setObjectTypeValue(Object value) throws XFormsException {
		if (!isMultiple()) {
			setSingleModelItemValue(value);
		} else {
			setModelItemsValue(value);
		}
	}
	
	protected void setSingleModelItemValue(Object value) throws XFormsException {
		String locationPath = getLocationPath();
		instance.setNodeValue(locationPath, value != null ? value.toString() : CoreConstants.EMPTY);
	}
	
	protected void setModelItemsValue(Object value) throws XFormsException {
		Instance instance = getInstance();
		String locationPath = getLocationPath();
		
		@SuppressWarnings("unchecked")
		Iterator<ModelItem> modelItems = instance.iterateModelItems(locationPath, false);
		
		String valueStr = value != null ? value.toString() : CoreConstants.EMPTY;
		
		while (modelItems.hasNext()) {
			ModelItem modelItem = modelItems.next();
			
			if (!modelItem.isReadonly()) {
				modelItem.setValue(valueStr);
				instance.getModel().addChanged((NodeImpl) modelItem.getNode());
			} else {
				getLogger().warn(this + " set node value: attempt to set readonly value");
			}
		}
	}
	
	protected void insertItems(NodeList items) throws XFormsException {
		Instance instance = getInstance();
		String pathExpression = getLocationPath();
		int contextSize = instance.countNodeset(pathExpression);
		String origin;
		String after;
		Element label;
		Element value;
		
		Node item;
		
		for (int i = 0; i < items.getLength(); i++) {
			origin = new StringBuffer(pathExpression).append('[').append(contextSize).append(']').toString();
			after = new StringBuffer(pathExpression).append('[').append(contextSize + 1).append(']').toString();
			
			instance.insertNode(origin, after);
			
			item = items.item(i);
			label = (Element) item.getFirstChild();
			value = (Element) item.getLastChild();
			
			instance.setNodeValue(new StringBuilder().append(origin).append(
			    CoreConstants.SLASH).append(value.getNodeName()).toString(),
			    value.getTextContent());
			instance.setNodeValue(new StringBuilder().append(origin).append(
			    CoreConstants.SLASH).append(label.getNodeName()).toString(),
			    label.getTextContent());
			
			contextSize++;
		}
	}
	
	protected void overrideExistingItems(NodeList items) throws XFormsException {
		Instance instance = getInstance();
		String pathExpression = getLocationPath();
		int contextSize = instance.countNodeset(pathExpression);
		String path;
		Element label;
		Element value;
		
		Node item;
		
		for (int i = 0; i < items.getLength(); i++) {
			path = new StringBuffer(pathExpression).append('[').append(contextSize).append(']').toString();
			
			instance.createNode(path);
			ModelItem modelItem = instance.getModelItem(path);
			if (modelItem == null) {
				throw new XFormsException("Model item for path '" + path + "' does not exist");
			}

			item = items.item(i);
			
			label = (Element) item.getFirstChild();
			value = (Element) item.getLastChild();
			if (setNode(path, instance, modelItem, label) == null) {
				break;
			}
			if (setNode(path, instance, modelItem, value) == null) {
				break;
			}
			
			instance.setNodeValue(new StringBuilder().append(path).append(CoreConstants.SLASH).append(value.getNodeName()).toString(), value.getTextContent());
			instance.setNodeValue(new StringBuilder().append(path).append(CoreConstants.SLASH).append(label.getNodeName()).toString(), label.getTextContent());
			
			contextSize++;
		}
	}
	
	private Node setNode(String path, Instance instance, ModelItem modelItem, Element element) throws XFormsException{
		try {
			instance.setNode(path, element);
		} catch (XFormsException e) {
			throw new XFormsException("Unable to add element " + element + " to " + path, e);
		} catch (Exception e) {
			if (modelItem.isReadonly()) {
				getLogger().warn("Model " + modelItem + " is read only!");
				return null;
			}
			
			Node importedNode = ((Element)modelItem.getNode()).getOwnerDocument().importNode(element, true);
			Node appendedChild = ((Element) modelItem.getNode()).appendChild(importedNode);
			instance.getModel().addChanged((NodeImpl) modelItem.getNode());
			
			return appendedChild;
		}
		return element;
	}
	
	protected String getInsertAttribute() {
		return insertAttribute;
	}
	
	protected void setInsertAttribute(String insertAttribute) {
		this.insertAttribute = insertAttribute;
	}
	
	protected boolean isInserting() {
		return getInsertAttribute() != null ? evalCondition(getElement(), getInsertAttribute()) : false;
	}
	
	protected boolean isMultiple() {
		return getMultipleAttribute() != null ? evalCondition(getElement(), getMultipleAttribute()) : false;
	}
	
	protected String getMultipleAttribute() {
		return multipleAttribute;
	}
	
	protected void setMultipleAttribute(String multipleAttribute) {
		this.multipleAttribute = multipleAttribute;
	}
	
	protected Instance getInstance() {
		if (instance == null)
			instance = this.model.getInstance(getInstanceId());
		
		return instance;
	}
	
	@Override
	public String getLocationPath() {
		if (locationPath == null)
			locationPath = super.getLocationPath();
		
		return locationPath;
	}
	
	protected String getValueAttribute() {
		return valueAttribute;
	}
	
	protected String getNodeValue() {
		return nodeValue;
	}
}