package com.idega.chiba.web.session.impl;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.chiba.adapter.ChibaEvent;
import org.chiba.adapter.DefaultChibaEventImpl;
import org.chiba.adapter.ui.UIGenerator;
import org.chiba.adapter.ui.XSLTGenerator;
import org.chiba.web.IWBundleStarter;
import org.chiba.web.servlet.WebUtil;
import org.chiba.web.session.XFormsSession;
import org.chiba.web.session.XFormsSessionManager;
import org.chiba.web.session.impl.DefaultXFormsSessionManagerImpl;
import org.chiba.web.session.impl.XFormsSessionBase;
import org.chiba.xml.events.XMLEvent;
import org.chiba.xml.xforms.config.XFormsConfigException;
import org.chiba.xml.xforms.exception.XFormsException;

import com.idega.block.web2.business.Web2Business;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;

/**
 * Idega implementation of a XFormsSessionBase.
 *
 * @author Anton Makarov</a>
 */

public class IdegaXFormsSessionBase extends XFormsSessionBase {

	public IdegaXFormsSessionBase(HttpServletRequest request,
								  HttpServletResponse response, 
								  HttpSession session)
			throws XFormsException {		
		super(request, response, session);
	}
	
	/**
     * processes the request after init.
     * @throws IOException
     * @throws XFormsException
     * @throws URISyntaxException
     */
    public synchronized void handleRequest() throws XFormsException {
        boolean updating=false; //this will become true in case ServletAdapter is in use
        updateLRU();

        WebUtil.nonCachingResponse(response);
        
        try {
            if (false && request.getMethod().equalsIgnoreCase("POST")) {
                updating=true;
                // updating ... - this is only called when ServletAdapter is in use
                ChibaEvent chibaEvent = new DefaultChibaEventImpl();
                chibaEvent.initEvent("http-request", null, request);
                adapter.dispatch(chibaEvent);
            }

            XMLEvent exitEvent = adapter.checkForExitEvent();
            if (exitEvent != null) {
                handleExit(exitEvent);
            }else{
                String referer = null;

                if(updating) {
                    // updating ... - this is only called when ServletAdapter is in use
                    referer = (String) getProperty(XFormsSession.REFERER);
                    setProperty("update","true");
                    String forwardTo = request.getContextPath() + "/view?sessionKey=" + getKey() + "&referer=" + referer;
                    response.sendRedirect(response.encodeRedirectURL(forwardTo));
                } else {
                    referer = request.getQueryString();

                    response.setContentType(WebUtil.HTML_CONTENT_TYPE);
                    //we got an initialization request (GET) - the session is not registered yet
                    UIGenerator uiGenerator = createUIGenerator();
                    //store UIGenerator in this session as a property
                    setProperty(XFormsSession.UIGENERATOR, uiGenerator);
                    //store queryString as 'referer' in XFormsSession
                    setProperty(XFormsSession.REFERER, request.getContextPath() + request.getServletPath() + "?" + referer);
                    //actually register the XFormsSession with the manager
                    getManager().addXFormsSession(this);
                    /*
                    the XFormsSessionManager is kept in the http-session though it is accessible as singleton. Subsequent
                    servlets should access the manager through the http-session attribute as below to ensure the http-session
                    is refreshed.
                    */
//                    httpSession.setAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER, DefaultXFormsSessionManagerImpl.createXFormsSessionManager(IdegaXFormSessionManagerImpl.class.getName()));

                    FacesContext context = FacesContext.getCurrentInstance();
                    
                    uiGenerator.setInput(this.getAdapter().getXForms());
                    uiGenerator.setOutput(context.getResponseWriter());
                    uiGenerator.generate();
                }
            }
        } catch (IOException e) {
            throw new XFormsException(e);
        } catch (URISyntaxException e) {
            throw new XFormsException(e);
        } 
        WebUtil.printSessionKeys(this.httpSession);
    }
	
    protected UIGenerator createUIGenerator() throws URISyntaxException, XFormsException {
    	
    	XSLTGenerator generator = (XSLTGenerator) super.createUIGenerator();
    	FacesContext context = FacesContext.getCurrentInstance();

		generator.setParameter("scriptPath", "/idegaweb/bundles/" + IWBundleStarter.BUNDLE_IDENTIFIER + ".bundle/resources/javascript/");
		generator.setParameter("imagesPath", "/idegaweb/bundles/" + IWBundleStarter.BUNDLE_IDENTIFIER + ".bundle/resources/style/images/");
		
		try {
			
			IWContext iwc = IWContext.getIWContext(context);
			Web2Business business = (Web2Business) IBOLookup.getServiceInstance(iwc, Web2Business.class);
			
			generator.setParameter("uriToMootoolsLib", business.getBundleURIToMootoolsLib());
			generator.setParameter("uriToPrototypeLib", business.getBundleURIToPrototypeLib());
			generator.setParameter("uriToScriptaculousLib", business.getBundleURIToScriptaculousLib());
			
			IWBundle bundle = getBundle((FacesContext)iwc, IWBundleStarter.BUNDLE_IDENTIFIER);
			IWResourceBundle iwrb = bundle.getResourceBundle(iwc);
	
			generator.setParameter("localization", iwrb.getLocalizedString("chiba.standard_layer_message", "Processing data"));
		
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return generator;
    }
    
    /**
     * returns the XFormsSessionManager used
     * @return the XFormsSessionManager used
     */
    public XFormsSessionManager getManager() {
        try {
			return DefaultXFormsSessionManagerImpl.createXFormsSessionManager(IdegaXFormSessionManagerImpl.class.getName());
		} catch (XFormsConfigException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public IWBundle getBundle(FacesContext ctx, String bundleIdentifier) {
    	
    	IWMainApplication iwma = IWMainApplication.getIWMainApplication(ctx);
		return iwma.getBundle(bundleIdentifier);
    }
}
