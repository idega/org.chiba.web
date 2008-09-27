package com.idega.chiba.web.xml.xforms.functions;

import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;
import org.chiba.xml.xforms.xpath.XFormsExtensionFunctions;

import com.idega.core.accesscontrol.business.LoginDBHandler;
/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.5 $
 *
 * Last modified: $Date: 2008/09/27 18:04:46 $ by $Author: civilis $
 */
public class IdegaExtensionFunctions {

    private IdegaExtensionFunctions() {
    }

    public static boolean userNameAlreadyExists(String userName) {
   
    	return LoginDBHandler.isLoginInUse(userName);
    }
    
    public static Pointer instance(ExpressionContext expressionContext, String modelId, String instanceId){
    	
    	if(modelId != null && modelId.length() != 0) {
    		
    		JXPathContext rootContext = expressionContext.getJXPathContext();
    		Variables vars = rootContext.getVariables();
    		
    		vars.declareVariable("contextmodel", modelId);
    		rootContext.setVariables(vars);
    		
    		return XFormsExtensionFunctions.instance(expressionContext, instanceId);
    	}
    	
    	return null;
    }
}