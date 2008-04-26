package com.idega.chiba.web.xml.xforms.connector.context;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.context.FacesContext;

import org.chiba.xml.xforms.connector.URIResolver;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;

import com.idega.util.CoreConstants;
import com.idega.util.xml.XmlUtil;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.7 $
 *
 * Last modified: $Date: 2008/04/26 02:21:37 $ by $Author: arunas $
 */
public class KeyContextResolver extends org.chiba.xml.xforms.connector.context.ContextResolver implements URIResolver {

	public static final String autofill_key_prefix = "fb-afk-";
	private static final String response_part1 = "<?xml version='1.0' encoding='UTF-8'?><data><";
	private static final String response_part2 = ">";
	private static final String response_part3 = "</";
	private static final String response_part4 = "></data>";
	private static final String faces_exp_part1 = "#{";
	private static final String faces_exp_part2 = "}";
	private static final String method_prefix = "get";
	private static final String type_name = "string";
	private static final String object_type = "collection";
	private Class<?> cls;
	private Method meth; 
	private String methodName;
	private String [] callprops;
	private String exp;
	
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
            if (key.indexOf(CoreConstants.MINUS)>0)
                 key = modifyKey(key);
            
            FacesContext ctx = FacesContext.getCurrentInstance();
            
            if(ctx == null)
            	return createResponseDocument(CoreConstants.EMPTY, "foobar").getDocumentElement();
            
            Object value = 
            	ctx.getApplication().createValueBinding(
            		new StringBuilder(faces_exp_part1)
            		.append(key)
            		.append(faces_exp_part2)
            		.toString()
            	).getValue(ctx);

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
        stream.close();
        return doc;
	}
    
    protected String objectToString (Collection<?> object) {
	
	String text = CoreConstants.EMPTY;
	cls = object.iterator().next().getClass();
	
	try {
	   meth =  cls.getMethod(methodName, null);
//	   collection-string 
	   if (callprops[1].equals(type_name)) {
	       for (Iterator<?> it = object.iterator(); it.hasNext();)    
		   text = text + meth.invoke(it.next(), null).toString() + CoreConstants.COMMA + CoreConstants.SPACE;
	       	text = text.substring(0, text.length()-2);	
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

    protected String modifyKey(String key) {
	
	callprops = key.split(CoreConstants.MINUS);
	exp = key.substring(key.lastIndexOf(CoreConstants.MINUS)+1);
//	collection type
	if (callprops[0].equals(object_type)){
	    key = exp.substring(0, exp.lastIndexOf(CoreConstants.DOT));
	    methodName = exp.substring(exp.lastIndexOf(CoreConstants.DOT)+1);
	    methodName = method_prefix + methodName.substring(0,1).toUpperCase() + methodName.substring(1, methodName.length()) ;
	}
	return key;
    }
}