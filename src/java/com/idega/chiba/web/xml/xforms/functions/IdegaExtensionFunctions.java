package com.idega.chiba.web.xml.xforms.functions;

import com.idega.core.accesscontrol.business.LoginDBHandler;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/09/19 11:54:00 $ by $Author: civilis $
 */
public class IdegaExtensionFunctions {

    private IdegaExtensionFunctions() {
    }

    public static boolean userNameAlreadyExists(String userName) {
   
    	System.out.println("__called usernamealreadyexists="+userName);
    	
    	return LoginDBHandler.isLoginInUse(userName);
    }
}