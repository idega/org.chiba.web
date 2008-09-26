package com.idega.chiba.web.xml.xforms.functions;

import java.util.Iterator;

import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.Pointer;
import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.xforms.Container;
import org.chiba.xml.xforms.core.Instance;
import org.chiba.xml.xforms.core.ModelItem;
import org.chiba.xml.xforms.xpath.ExtensionFunctionsHelper;

import com.idega.core.accesscontrol.business.LoginDBHandler;
/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/09/26 16:57:52 $ by $Author: civilis $
 */
public class IdegaExtensionFunctions {

    private IdegaExtensionFunctions() {
    }

    public static boolean userNameAlreadyExists(String userName) {
   
    	return LoginDBHandler.isLoginInUse(userName);
    }
    
    public static boolean validate (ExpressionContext expressionContext, String idref){
    	  // get chiba container
        Container container = ExtensionFunctionsHelper.getChibaContainer(expressionContext);
        if (container == null) {
            return false;
        }

        // lookup instance object
        Object object = container.lookup(idref);
        if (object != null && object instanceof Instance) {
            // get root element node pointer
        	Instance instance = (Instance) object;

        	Pointer point = ((Instance) object).getPointer(org.chiba.xml.xpath.XPathUtil.OUTERMOST_CONTEXT);
              
			System.out.println(point.toString());

        	Iterator<?> iterator = instance.iterateModelItems(org.chiba.xml.xpath.XPathUtil.OUTERMOST_CONTEXT);
            ModelItem modelItem;
            while (iterator.hasNext()) {
                modelItem = (ModelItem) iterator.next();
                System.out.println("items=" +modelItem.getValue());
            }
        	
                  	DOMUtil.prettyPrintDOM(instance.getElement());
        	 
            return false;
        }

        return false;

    }
    
   
}