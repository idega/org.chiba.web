package com.idega.chiba.web.xml.xforms.ui;

import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import com.idega.chiba.web.xml.xforms.ui.state.IdegaBoundElementState;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.xml.XPathUtil;
import com.idega.util.xml.XmlUtil;

public class IdegaOutput extends Output {

	private static final Logger LOGGER = Logger.getLogger(IdegaOutput.class.getName());

	private String resolver;

	private Boolean pdfView = null;

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

	@Override
	public Object computeValueAttribute() throws XFormsException {
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
					if (i + 1 < map.getLength())
						attributes.append("; ");
				}
			}
			LOGGER.log(Level.WARNING, "Error while trying to resolve value for the output element: ID: " + this.id + ", attributes: " + attributes.toString(), e);
		}
		if (value != null) {
			if (StringHandler.isNumeric(value.toString())) {
				String valueAttribute = getValueAttribute();
				if (!StringUtil.isEmpty(valueAttribute) && valueAttribute.indexOf("translate") != -1) {
					String property = IWMainApplication.getDefaultIWMainApplication().getSettings().getProperty("xform_increase_number_value");
					if (!StringUtil.isEmpty(property)) {
						String[] ids = property.split(CoreConstants.COMMA);
						for (String id: ids) {
							if (!StringUtil.isEmpty(id) && id.equals(this.id)) {
								Integer number = null;
								try {
									number = Integer.valueOf(value.toString());
								} catch (NumberFormatException e) {
									LOGGER.warning("Error converting " + value + " to number");
								}
								if (number != null) {
									number++;
									value = String.valueOf(number);
								}
							}
						}
					}
				}
			}

			return value;
		}

        String pathExpression = BindingResolver.getExpressionPath(this, this.repeatItemId);
        Instance instance = this.model.getInstance(this.model.computeInstanceId(pathExpression));
        if (!instance.existsNode(pathExpression)) {
            return null;
        }

        String valueAttribute = getValueAttribute();

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

	@Override
	protected UIElementState createElementState() throws XFormsException {
        if (isBound())
            return new IdegaBoundElementState();

        return super.createElementState();
    }

}