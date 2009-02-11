package com.idega.chiba.web.exception;

import org.chiba.web.flux.FluxException;

/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2009/02/11 11:45:32 $ by $Author: arunas $
 */

public class SessionExpiredException extends FluxException {

	private static final long serialVersionUID = 3701124290017696348L;

	public SessionExpiredException (){
	}
	
	public SessionExpiredException (String msg){
		 super(msg);
	}
  

}
