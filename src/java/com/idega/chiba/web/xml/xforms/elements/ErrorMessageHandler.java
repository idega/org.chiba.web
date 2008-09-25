package com.idega.chiba.web.xml.xforms.elements;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.chiba.web.IWBundleStarter;
import org.chiba.xml.xforms.Container;
import org.chiba.xml.xforms.core.ModelItem;
import org.w3c.dom.events.EventTarget;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.3 $
 * 
 *          Last modified: $Date: 2008/09/25 14:03:01 $ by $Author: civilis $
 * 
 */
public interface ErrorMessageHandler {

	public static final String validationErrorType = "idega-validation-error";
	public static final String messageContextAtt = "message";
	public static final String targetContextAtt = "target";
	public static final String errorTypeContextAtt = "errType";

	public enum ErrorType {
		required {
			public String getDefaultErrorMessage(Locale locale) {
				return getBundle(locale).getLocalizedString("xforms.validation.requiredEmpty", "Required field is empty");
			}
		},
		validation {
			public String getDefaultErrorMessage(Locale locale) {
				return getBundle(locale).getLocalizedString("xforms.validation.invalid", "Value provided is not valid");
			}
		},
		constraint {
			public String getDefaultErrorMessage(Locale locale) {
				return getBundle(locale).getLocalizedString("xforms.validation.constraintBroken", "The constraint was not met");
			}
		},
		custom {
			public String getDefaultErrorMessage(Locale locale) {
				return getBundle(locale).getLocalizedString("xforms.validation.customDefault", "Field hasn't passed the validation");
			}
		};

		public abstract String getDefaultErrorMessage(Locale locale);

		private static IWResourceBundle getBundle(Locale locale) {

			IWMainApplication iwma;
			IWContext iwc = IWContext.getCurrentInstance();

			if (iwc != null)
				iwma = iwc.getIWMainApplication();
			else
				iwma = IWMainApplication.getDefaultIWMainApplication();

			IWBundle iwb = iwma.getBundle(IWBundleStarter.BUNDLE_IDENTIFIER);
			return iwb.getResourceBundle(locale);
		}
		
		private static Map<String, ErrorType> allStringTypesEnumsMappings;
		
		static {
			allStringTypesEnumsMappings = new HashMap<String, ErrorType>();
			
			for (ErrorType type : values())
				allStringTypesEnumsMappings.put(type.toString(), type);
		}
		
		public static ErrorType getByStringRepresentation(String type) {
			return allStringTypesEnumsMappings.get(type);
		}
	}

	public abstract void send(ModelItem mi, Container container,
			EventTarget target, String componentId, String message,
			ErrorType errType);
}