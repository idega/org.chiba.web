package com.idega.chiba.facade;

import org.chiba.web.flux.FluxException;
import org.chiba.web.flux.FluxFacade;
import org.chiba.xml.dom.DOMUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.idega.util.xml.XPathUtil;

/**
 * 
 * 
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version Revision: 1.0
 * 
 * Last modified: May 27, 2008 by Author: Anton
 * 
 */
@Scope("request")
@Service("fluxexhand")
public class FluxExceptionHandler extends FluxFacade {
	
//	private static XPathUtil submitErrorElement;

	public FluxExceptionHandler() {
		super();
	}

	public Element fireAction(String id, String sessionKey) {
		Element element = null;
		try {
			element = super.fireAction(id, sessionKey);
		} catch (Exception e) {
			System.out.println("----------------------Exception in flux: fireAction --------------------");
			e.printStackTrace();
    	} finally {
			DOMUtil.prettyPrintDOM(element);
    		return element;
		}
	}

	public Element setXFormsValue(String id, String value, String sessionKey)
			throws FluxException {
		Element element = null;
		try {
			element = super.setXFormsValue(id, value, sessionKey);
		} catch (FluxException e) {
			System.out.println("----------------------Exception in flux: setXFormsValue --------------------");
			e.printStackTrace();
		} finally {
			DOMUtil.prettyPrintDOM(element);
			return element;
		}
	}

	public Element setRepeatIndex(String id, String position, String sessionKey)
			throws FluxException {
		Element element = null;
		try {
			element = super.setRepeatIndex(id, position, sessionKey);
		} catch (FluxException e) {
			System.out.println("----------------------Exception in flux: setRepeatIndex --------------------");
			e.printStackTrace();
		} finally {
			DOMUtil.prettyPrintDOM(element);
			return element;
		}
	}
	
	public Element fetchProgress(String id, String filename, String sessionKey) {
		Element element = null;
		try {
			element = super.fetchProgress(id, filename, sessionKey);
		} catch (Exception e) {
			System.out.println("----------------------Exception in flux --------------------");
			
		} finally {
			DOMUtil.prettyPrintDOM(element);
			return element;
		}
	}
	
	public void keepAlive(String sessionKey) {
		try {
			super.keepAlive(sessionKey);
		} catch (Exception e) {
			//TODO implement exception handling
		}
	}
	 
    public void close(String sessionKey){
		try {
			super.close(sessionKey);
		} catch (Exception e) {
			//TODO implement exception handling
		}
    }
    
//    private boolean hasErrors(Node context) {
//    		
//		if(submitErrorElement == null)
//			submitErrorElement = new XPathUtil(".//xf:setvalue[@model='data_model']");
//		
//		Element errorElement =  (Element)submitErrorElement.getNode(context);
//		
//		return (errorElement == null) ? false : true;
//    }
}
