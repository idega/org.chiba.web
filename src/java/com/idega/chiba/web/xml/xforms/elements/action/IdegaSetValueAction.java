package com.idega.chiba.web.xml.xforms.elements.action;

import java.util.Iterator;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
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

import com.idega.chiba.web.xml.xforms.functions.IdegaExtensionFunctions;
import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.text.Item;
import com.idega.util.text.TableRecord;

public class IdegaSetValueAction extends SetValueAction {

	private static final Logger LOGGER = Logger.getLogger(IdegaSetValueAction.class.getName());

	private static final String INSERT_ATTRIBUTE = "insert";
	private static final String MULTIPLE_ATTRIBUTE = "multiple";
	private static final String placeResponseDocument_ATTRIBUTE = "placeResponseDocument";

	private String nodeValue;
	private String valueAttribute;
	private String insertAttribute;
	private String multipleAttribute;
	private String ifCondition;
	private String placeResponseDocumentAttribute;

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
		this.ifCondition = getXFormsAttribute("idega-if");

		setPlaceResponseDocumentAttribute(getXFormsAttribute(placeResponseDocument_ATTRIBUTE));
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
			LOGGER.warning("perform: nodeset '" + locationPath + "' is empty");
			return false;
		} else
			return true;
	}

	private boolean canPerform() {
		boolean perform = true;
		if (StringUtil.isEmpty(ifCondition)) {
			return perform;
		}

		Object result = null;
		try {
			String beanExpEnd = "',";
			String beanExp = ifCondition
					.substring(
							0,
							ifCondition.indexOf(beanExpEnd)
									+ (beanExpEnd.length() - 1));
			beanExp = beanExp.replaceAll("'", CoreConstants.EMPTY);

			String params = null;
			int separator = ifCondition.indexOf(beanExpEnd);
			if (separator != -1) {
				params = ifCondition.substring(separator + beanExpEnd.length());
			}

			result = IdegaExtensionFunctions.resolveExpressionByInstance(
					getInstance(), beanExp, params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result instanceof Boolean) {
			perform = (Boolean) result;
		}
		return perform;
	}

	@Override
	public void perform() throws XFormsException {
		if (!isNodeExists()) {
			return;
		}

		if (!canPerform()) {
			return;
		}

		String valueAttribute = getValueAttribute();
		if (valueAttribute != null) {
			Object value = getValue();
			setValue(value);
		} else {
			String nodeValue = getNodeValue();
			setValue(nodeValue);
		}
		updateBehavior();
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
		context.getVariables().declareVariable("currentContextPath",
				currentPath);
		if (ELUtil.isExpression(valueAttribute)){
			try{
				FacesContext facesContext = FacesContext.getCurrentInstance();
				return facesContext.getApplication().evaluateExpressionGet(facesContext, valueAttribute, String.class);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			String locationPath = getLocationPath();
			context.getPointer(locationPath + "[chiba:declare('node-value', "
					+ valueAttribute + ")]");
		} catch (Exception e) {
			throw new XFormsComputeException("invalid value expression at "
					+ this, e, this.target, valueAttribute);
		}

		Object value = context.getValue("chiba:undeclare('node-value')");

		if (value == null) {
//			Checking if value is not provided with XPath expression
			try {
				value = (new com.idega.util.xml.XPathUtil(valueAttribute)).getNode(this.element).getTextContent();
			} catch (Exception e) {}
		}

		// check for string conversion to prevent something like "5 + 0" to be
		// evaluated to "5.0"
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

	protected void setDocumentTypeValue(Document itemListDoc)
			throws XFormsException {

		final NodeList children = itemListDoc.getDocumentElement()
				.getChildNodes();

		if (isInserting()) {

			insertItems(children);

		} else if (isPlaceResponseDocument()) {

			placeResponseDocumentChildren(children);

		} else {

			overrideExistingItems(children);
		}
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
		instance.setNodeValue(locationPath, value != null ? value.toString()
				: CoreConstants.EMPTY);
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
				LOGGER.warning("set node value: attempt to set readonly value");
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
			origin = new StringBuffer(pathExpression).append('[')
					.append(contextSize).append(']').toString();
			after = new StringBuffer(pathExpression).append('[')
					.append(contextSize + 1).append(']').toString();

			instance.insertNode(origin, after);

			item = items.item(i);
			label = (Element) item.getFirstChild();
			value = (Element) item.getLastChild();

			instance.setNodeValue(
					new StringBuilder().append(origin)
							.append(CoreConstants.SLASH)
							.append(value.getNodeName()).toString(),
					value.getTextContent());
			instance.setNodeValue(
					new StringBuilder().append(origin)
							.append(CoreConstants.SLASH)
							.append(label.getNodeName()).toString(),
					label.getTextContent());

			contextSize++;
		}
	}

	private void placeResponseDocumentChildren(NodeList children)
			throws XFormsException {

		final Instance instance = getInstance();
		final String pathExpression = getLocationPath();

		for (int i = 0; i < children.getLength(); i++) {

			final Element child = (Element) children.item(i);

			final String nodePath = new StringBuffer(pathExpression)
					.append(CoreConstants.SLASH).append(child.getNodeName())
					.toString();

			if (!instance.existsNode(nodePath)) {

				instance.createNode(nodePath);
				instance.setNode(nodePath, child);

			}

			instance.setNodeValue(nodePath, child.getTextContent());
		}
	}

	/**
	 * <p>Overrides only "tableRow" tags. It only appends existing lists
	 * in other cases.</p>
	 * @param items {@link Item}, {@link TableRecord} or other elements, that
	 * has list of elements inside with their values defined, ot not.
	 * @throws XFormsException
	 */
	protected void overrideExistingItems(NodeList items) throws XFormsException {

		Instance instance = getInstance();
		String pathExpression = getLocationPath();
		int contextSize = instance.countNodeset(pathExpression);

		String path;
		Node item;

		int contextSize2 = contextSize;

		for (int i = 0; i < items.getLength(); i++) {
			path = new StringBuffer(pathExpression).append('[')
					.append(contextSize).append(']').toString();

			if (!instance.existsNode(path)) {
				instance.createNode(path);
			}

			ModelItem modelItem = instance.getModelItem(path);
			if (modelItem == null) {
				throw new XFormsException("Model item for path '" + path
						+ "' does not exist");
			}

			item = items.item(i);

			NodeList nl = item.getChildNodes();
			for (int e = 0; e < nl.getLength(); e++) {
				if (setNode(path, instance, modelItem, (Element) nl.item(e)) == null) {
					break;
				}

				instance.setNodeValue(
						new StringBuilder().append(path)
								.append(CoreConstants.SLASH)
								.append(nl.item(e).getNodeName()).toString(),
						nl.item(e).getTextContent());
			}

			contextSize++;
		}

		/*
		 * This piece is for deletion of old values. Sorry, but only for
		 * "tabelRow" now.
		 */
		if (contextSize2 <= 1) {
			LOGGER.warning("perform: nodeset '" + pathExpression + "' is empty");
		} else {

			String pathExpression2 = getLocationPath();

			if (!pathExpression2.contains("tableRow")) {
				return;
			}

			pathExpression2 = pathExpression2.split("'")[1];
			Model dataModel = getModel().getContainer().getModel("data_model");
			Instance instance2 = dataModel.getInstance(pathExpression2);

			String pathDeletion = null;
			for (int attribute = 1; attribute < contextSize2; attribute++) {
				// since jxpath doesn't provide a means for evaluating an
				// expression
				// in a certain context, we use a trick here: the expression
				// will be
				// evaluated during getPointer and the result stored as a
				// variable
				JXPathContext context = instance2.getInstanceContext();
				boolean lenient = context.isLenient();
				context.setLenient(true);
				context.getPointer(pathExpression
						+ "[chiba:declare('delete-position', " + 2
						+ ")]");
				context.setLenient(lenient);

				// since jxpath's round impl is buggy (returns 0 for NaN) we
				// perform 'round' manually
				double value = ((Double) context
						.getValue("number(chiba:undeclare('delete-position'))"))
						.doubleValue();
				long position = Math.round(value);
				if (Double.isNaN(value) || position < 1 || position > contextSize) {
					LOGGER.warning("perform: expression '" + 2 + "' does not point to an existing node");
					return;
				}

				pathDeletion = new StringBuffer(pathExpression).append('[')
						.append(position).append(']').toString();

				if (instance2.existsNode(pathDeletion)) {
					try {
						instance2.deleteNode(pathDeletion);
						LOGGER.info("Node deleted in override action:" + pathDeletion);
					} catch (JXPathException e) {
						LOGGER.warning("Unable to delete:" + pathDeletion);
					}
				}
			}
		}
	}

	private Node setNode(String path, Instance instance, ModelItem modelItem,
			Element element) throws XFormsException {
		try {
			instance.setNode(path, element);

			// System.out.println("________Appended node to modelitem");
			// DOMUtil.prettyPrintDOM((Node) modelItem.getNode());
			// System.out.println("/________Appended node to modelitem");

		} catch (XFormsException e) {
			throw new XFormsException("Unable to add element " + element
					+ " to " + path, e);
		} catch (Exception e) {
			if (modelItem.isReadonly()) {
				LOGGER.warning("Model " + modelItem + " is read only!");
				return null;
			}

			Element currentElement = (Element) modelItem.getNode();
			Node importedNode = currentElement.getOwnerDocument().importNode(
					element, true);
			Node appendedChild = currentElement.appendChild(importedNode);
			instance.getModel().addChanged((NodeImpl) currentElement);

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
		return getInsertAttribute() != null ? evalCondition(getElement(),
				getInsertAttribute()) : false;
	}

	/**
	 * @return if the returned value (xml) should be placed into the nodeset
	 *         overriding all it's children
	 */
	private boolean isPlaceResponseDocument() {

		return getPlaceResponseDocumentAttribute() != null ? evalCondition(
				getElement(), getPlaceResponseDocumentAttribute()) : false;
	}

	protected boolean isMultiple() {
		return getMultipleAttribute() != null ? evalCondition(getElement(),
				getMultipleAttribute()) : false;
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

	public String getPlaceResponseDocumentAttribute() {
		return placeResponseDocumentAttribute;
	}

	public void setPlaceResponseDocumentAttribute(
			String placeResponseDocumentAttribute) {
		this.placeResponseDocumentAttribute = placeResponseDocumentAttribute;
	}
}

