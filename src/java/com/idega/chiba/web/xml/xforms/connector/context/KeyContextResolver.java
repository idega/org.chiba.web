package com.idega.chiba.web.xml.xforms.connector.context;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.beanutils.BeanUtils;
import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.xforms.connector.URIResolver;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;

import com.idega.util.CoreConstants;
import com.idega.util.expression.ELUtil;
import com.idega.util.xml.XmlUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.14 $
 *
 * Last modified: $Date: 2008/11/22 09:22:55 $ by $Author: arunas $
 */
public class KeyContextResolver extends org.chiba.xml.xforms.connector.context.ContextResolver implements URIResolver {

	public static final String autofill_key_prefix = "fb-afk-";
	private static final String response_part1 = "<?xml version='1.0' encoding='UTF-8'?><data><";
	private static final String response_part2 = ">";
	private static final String response_part3 = "</";
	private static final String response_part4 = "></data>";
	private String [] callprops;
	private String methodName;
	

	/**
	 * resolves object, which is configured in the faces-config.xml, method value
	 */
    @Override
		public Object resolve() throws XFormsException {
    	
        try {
        	String xpath = new URI(getURI()).getSchemeSpecificPart();

            if(!xpath.startsWith(autofill_key_prefix))
            	return super.resolve();
            
            String key = xpath.substring(autofill_key_prefix.length());
            
            if (key.startsWith(ConstantTypes.COLLECTION_TYPE.toString()))
                 key = modifyKey(key);
            
            Object value = ELUtil.getInstance().getBean(key);
            
	        return createResponseDocument(value == null ? CoreConstants.EMPTY : value instanceof String ? (String)value : value instanceof Collection ? objectToString((Collection<?>)value) : value.toString(), xpath).getDocumentElement();
        } catch (Exception e) {
        	
        	try {
        		return createResponseDocument(CoreConstants.EMPTY, "foobar").getDocumentElement();
			} catch (Exception e2) {
				throw new XFormsException(e2);
			}
        }
    }
    
    protected Document createResponseDocument(String response_text, String key) throws Exception {
		
//      TODO: optimize this if needed (e.g. add caching or smth) 
        String response_xml = 
        	new StringBuilder(response_part1)
        	.append(key)
        	.append(response_part2)
        	.append(response_text)
        	.append(response_part3)
        	.append(key)
        	.append(response_part4)
        	.toString();
        
        InputStream stream = new ByteArrayInputStream(response_xml.getBytes(CoreConstants.ENCODING_UTF8));
        Document doc = XmlUtil.getDocumentBuilder().parse(stream);
        DOMUtil.prettyPrintDOM(doc);
        stream.close();
        return doc;
	}
    
    private String objectToString (Collection<?> object) {
    	
	
	String text = CoreConstants.EMPTY;
		
	try {
		String typeProp = getCallprops()[1];
		
		
 
	   if (typeProp.equals(ConstantTypes.STRING_TYPE.toString())) {
		   //	collection-string
		   String value;
		   
	       for (Iterator<?> it = object.iterator(); it.hasNext();)    {
	    	   
		    	value = BeanUtils.getProperty(it.next(), getMethodName());
		    	
		    	 if (!CoreConstants.EMPTY.equals(value))
				       text = text + value + CoreConstants.COMMA + CoreConstants.SPACE;
		    	 
	       }
	       
	       if (!CoreConstants.EMPTY.equals(text))
	       		text = text.substring(0, text.lastIndexOf(CoreConstants.COMMA));	
	       
	   } else if (typeProp.equals(ConstantTypes.LIST_TYPE.toString())) {
		   
//		   TODO collection-list
//		   MethodUtils.invokeMethod(object, text, );
//			PropertyUtils.getProperty(bean, name);

		   
	   }
	   
	   
	}catch (NoSuchMethodException e) {
	    e.printStackTrace();  
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	} catch (InvocationTargetException e) {
	    e.printStackTrace();
	}      

		return text;
    }

    private String modifyKey(String key) {
	
    	setCallprops(key.split(CoreConstants.MINUS));
    	
    	String beanExp = getCallprops()[2];
    	key = beanExp.substring(0, beanExp.lastIndexOf(CoreConstants.DOT));
    	
    	setMethodName(beanExp.substring(beanExp.lastIndexOf(CoreConstants.DOT) + 1));
    	
    	return key;
	
    }

	private String[] getCallprops() {
		return callprops;
	}

	private void setCallprops(String[] callprops) {
		this.callprops = callprops;
	}
	
	private String getMethodName() {
		return methodName;
	}

	private void setMethodName(String methodName) {
		this.methodName = methodName;
	}
}