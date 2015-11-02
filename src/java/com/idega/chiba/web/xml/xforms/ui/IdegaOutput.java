package com.idega.chiba.web.xml.xforms.ui;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jxpath.JXPathContext;
import org.chiba.xml.ns.NamespaceResolver;
import org.chiba.xml.xforms.core.BindingResolver;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.Model;
import org.chiba.xml.xforms.exception.XFormsComputeException;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xforms.ui.Output;
import org.chiba.xml.xforms.ui.UIElementState;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.idega.chiba.web.xml.xforms.functions.IdegaExtensionFunctions;
import com.idega.chiba.web.xml.xforms.ui.state.IdegaBoundElementState;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.ListUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.xml.XPathUtil;
import com.idega.util.xml.XmlUtil;

public class IdegaOutput extends Output {

	private static final Logger LOGGER = Logger.getLogger(IdegaOutput.class.getName());

	private String resolver;

	private Boolean pdfView = null;

	private String ifCondition;

	public IdegaOutput(Element element, Model model) {
        super(element, model);

        this.xformsPrefix = NamespaceResolver.getPrefix(this.element, XPathUtil.IDEGA_XFORM_NS);

        //	Fixing IDs for repeated items
		if (XmlUtil.getParentElement(element, REPEAT) != null) {
			String id = this.container.generateId();
			try {
				setGeneratedId(id);
			} catch (XFormsException e) {
				LOGGER.log(Level.WARNING, "Unable to set generated id: " + id + " for IdegaOutput", e);
			}

			Element parentElement = XmlUtil.getParentElement(element, GROUP);
			if (parentElement != null) {
				try {
					setRepeatItemId(parentElement.getAttribute("id"));
				} catch (XFormsException e) {
					LOGGER.log(Level.WARNING, "Unable to set repeat-item id", e);
				}
			}
		}
    }

	@Override
	public void init() throws XFormsException {
		super.init();

		this.resolver = getXFormsAttribute("resolver");
		this.ifCondition = getXFormsAttribute("idega-if");
	}

	public Boolean getPdfView() {
		if (pdfView == null) {
			Instance controlInstance = getModel().getInstance("control-instance");
			if (controlInstance != null) {
				String value = null;
				try {
					value = controlInstance.getNodeValue("instance('control-instance')/generatePdf");
				} catch (Exception e) {
					e.printStackTrace();
				}

				Boolean pdf = Boolean.valueOf(value);
				setPdfView(pdf == null ? false : pdf);
			}
		}
		return pdfView;
	}

	public void setPdfView(Boolean pdfView) {
		this.pdfView = pdfView;
	}

	@Override
	protected void dispatchValueChangeSequence() throws XFormsException {
		super.dispatchValueChangeSequence();
	}

	public String getResolver() {
		return resolver;
	}

	public void setResolver(String resolver) {
		this.resolver = resolver;
	}

	private boolean canPerform() {
		boolean perform = true;
		if (StringUtil.isEmpty(ifCondition)) {
			return perform;
		}

		Object result = null;
		try {
			String beanExpEnd = "',";
			String beanExp = ifCondition.substring(
							0,
							ifCondition.indexOf(beanExpEnd) + (beanExpEnd.length() - 1));
			beanExp = beanExp.replaceAll("'", CoreConstants.EMPTY);

			String params = null;
			int separator = ifCondition.indexOf(beanExpEnd);
			if (separator != -1) {
				params = ifCondition.substring(separator + beanExpEnd.length());
			}

			result = IdegaExtensionFunctions.resolveExpressionByInstance(this.model.getInstance(getInstanceId()), beanExp, params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result instanceof Boolean) {
			perform = (Boolean) result;
		}
		return perform;
	}

	@Override
	public Object computeValueAttribute() throws XFormsException {
		String valueAttribute = getValueAttribute();
		if (!StringUtil.isEmpty(valueAttribute) && valueAttribute.indexOf("translate") != -1) {
			IWMainApplicationSettings settings = IWMainApplication.getDefaultIWMainApplication().getSettings();
			boolean changeValue = true;
			if (settings.getBoolean("xform_increase_nr_value_pdf", false) && !getPdfView()) {
				changeValue = false;
			}

			if (changeValue) {
				List<String> properties = Arrays.asList(settings.getProperty("xform_increase_number_value").split(CoreConstants.COMMA));
				if (!ListUtil.isEmpty(properties) && properties.contains(id)) {
					List<String> nodes = getNodes(valueAttribute);
					if (!StringUtil.isEmpty(valueAttribute)) {
						Instance data = model.getInstance("data-instance");
						Map<String, String> values = new HashMap<>();
						for (String node: nodes) {
							String path = "instance('data-instance')/" + node;
							String nodeValue = data.getNodeValue(path);

							values.put(node, nodeValue);

							if (!StringUtil.isEmpty(nodeValue)) {
								Integer number = null;
								try {
									number = Integer.valueOf(nodeValue.toString());
								} catch (NumberFormatException e) {
									LOGGER.warning("Error converting " + nodeValue + " to number");
								}
								if (number != null && number == -1) {
									number++;
									nodeValue = String.valueOf(number);

									data.setNodeValue(path, nodeValue);
								}
							}
						}
						nodes.removeAll(values.keySet());
						for (String nodeWithoutValue: nodes) {
							data.setNodeValue("instance('data-instance')/" + nodeWithoutValue, String.valueOf(0));
						}

						LOGGER.info("Values: " + values);
					}
				}
			}
		}

		Object value = null;
		try {
			value = super.computeValueAttribute();
		} catch (Exception e) {
			StringBuilder attributes = new StringBuilder();
			NamedNodeMap map = element.getAttributes();
			if (map != null) {
				for (int i = 0; i < map.getLength(); i++) {
					Node attribute = map.item(i);
					attributes.append(attribute.toString());
					if (i + 1 < map.getLength()) {
						attributes.append("; ");
					}
				}
			}
			LOGGER.log(Level.WARNING, "Error while trying to resolve value for the output element: ID: " + this.id + ", attributes: " + attributes.toString(), e);
		}

		if (!StringUtil.isEmpty(valueAttribute) && valueAttribute.indexOf("resolveExpression") != -1) {
			try {
				if (canPerform()) {
					String beanExpEnd = CoreConstants.QOUTE_SINGLE_MARK + CoreConstants.COMMA;
					if (valueAttribute.indexOf(beanExpEnd) == -1) {
						beanExpEnd = CoreConstants.QOUTE_SINGLE_MARK + CoreConstants.BRACKET_RIGHT;
					}
					String beanExp = valueAttribute.substring(0, valueAttribute.indexOf(beanExpEnd) + (beanExpEnd.length() - 1));
					beanExp = beanExp.replaceAll(CoreConstants.QOUTE_SINGLE_MARK, CoreConstants.EMPTY);
					beanExp = StringHandler.replace(beanExp, "idega:resolveExpression(", CoreConstants.EMPTY);

					String params = null;
					int separator = valueAttribute.indexOf(beanExpEnd);
					if (separator != -1) {
						params = valueAttribute.substring(separator + beanExpEnd.length());
					}
					if (params != null) {
						params = params.trim();

						if (params.startsWith(CoreConstants.QOUTE_SINGLE_MARK)) {
							params = params.substring(1);
						}

						if (params.endsWith(CoreConstants.BRACKET_RIGHT)) {
							params = params.substring(0, params.length() - 1);
						}
						if (params.endsWith(CoreConstants.QOUTE_SINGLE_MARK)) {
							params = params.substring(0, params.length() - 1);
						}
					}
					value = IdegaExtensionFunctions.resolveExpressionByInstance(this.model.getInstance(getInstanceId()), beanExp, params);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

        String pathExpression = BindingResolver.getExpressionPath(this, this.repeatItemId);
        Instance instance = this.model.getInstance(this.model.computeInstanceId(pathExpression));
        if (!instance.existsNode(pathExpression)) {
            return null;
        }

        valueAttribute = getValueAttribute();

        JXPathContext context = instance.getInstanceContext();

        String currentPath = getParentContextPath(this.element);
        context.getVariables().declareVariable("currentContextPath", currentPath);
        context.getVariables().declareVariable("contextmodel", getModelId());
        try {
            context.getPointer(pathExpression + "[chiba:declare('output-value', " + valueAttribute + ")]");
        } catch (Exception e) {
            throw new XFormsComputeException("invalid value expression at " + this, e, this.target, valueAttribute);
        }
        value = context.getValue("chiba:undeclare('output-value')");
        context.getVariables().undeclareVariable("currentContextPath");
        context.getVariables().undeclareVariable("contextmodel");

        // check for string conversion to prevent sth. like "5 + 0" to be evaluated to "5.0"
        if (value instanceof Double) {
        	Double originalValue = (Double) value;
            // Additionally check for special cases
            double doubleValue = ((Double) value).doubleValue();
            if (!(Double.isNaN(doubleValue) || Double.isInfinite(doubleValue))) {
            	try {
            		value = NumberFormat.getInstance().format(doubleValue);
            		String valueExpression = "string(" + value + ")";
            		while (originalValue >= 1000 && valueExpression.indexOf(CoreConstants.DOT) != -1) {
            			valueExpression = valueExpression.substring(0, valueExpression.lastIndexOf(CoreConstants.DOT)) +
            				valueExpression.substring(valueExpression.lastIndexOf(CoreConstants.DOT) + 1);
            			originalValue = originalValue / 1000;
            		}
            		value = context.getValue(valueExpression);
            	} catch (Exception e) {
            		String message = "Error while trying to get a string value from '" + value + "' using XPath";
            		LOGGER.log(Level.WARNING, message, e);
            		CoreUtil.sendExceptionNotification(message, e);
            	}
            }
        }

        return value;
    }

	private static final Pattern nodeSetPattern = Pattern.compile("([A-Z])\\w+");
	private static final Pattern lowerCaseNodeSetPattern = Pattern.compile("([a-z])\\w+\\,");

	private List<String> getNodes(String valueAttribute) {
		Map<String, Boolean> nodes = new HashMap<>();
		if (StringUtil.isEmpty(valueAttribute)) {
			return new ArrayList<>(nodes.keySet());
		}

		Matcher matcher = nodeSetPattern.matcher(valueAttribute);
		while (matcher.find()) {
			nodes.put(valueAttribute.substring(matcher.start(), matcher.end()), Boolean.TRUE);
		}

		matcher = lowerCaseNodeSetPattern.matcher(valueAttribute);
		while (matcher.find()) {
			int start = matcher.start();
			if (valueAttribute.substring(start - 1, start).equals(CoreConstants.BRACKET_LEFT)) {
				String node = valueAttribute.substring(start, matcher.end());
				if (node.endsWith(CoreConstants.COMMA)) {
					node = node.substring(0, node.length() - 1);
				}
				nodes.put(node, Boolean.TRUE);
			}
		}

		return new ArrayList<>(nodes.keySet());
	}

	@Override
	protected UIElementState createElementState() throws XFormsException {
        if (isBound())
            return new IdegaBoundElementState();

        return super.createElementState();
    }

}