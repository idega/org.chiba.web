/*
 *  XFormsFilter
 *  Copyright (C) 2006 Adam Retter, Devon Portal Project <adam.retter@devon.gov.uk>
 */

package com.idega.chiba.web.xml.xslt.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chiba.web.WebAdapter;
import org.chiba.web.WebFactory;
import org.chiba.web.filter.BufferedHttpServletResponseWrapper;
import org.chiba.web.servlet.WebUtil;
import org.chiba.web.session.XFormsSession;
import org.chiba.web.session.XFormsSessionManager;
import org.chiba.xml.ns.NamespaceConstants;
import org.chiba.xml.xforms.config.Config;
import org.chiba.xml.xforms.config.XFormsConfigException;
import org.chiba.xml.xforms.exception.XFormsException;

import com.idega.idegaweb.IWMainApplication;
import com.idega.servlet.filter.IWBundleResourceFilter;

/**
 * A Servlet Filter to provide XForms functionality to existing Servlets
 *
 * @author Adam Retter <adam.retter@devon.gov.uk>, Joern Turner
 * @version 1.3
 * @serial 2007-02-19T13:51
 */
public class XFormsFilter extends org.chiba.web.filter.XFormsFilter {
    private static final Log LOG = LogFactory.getLog(XFormsFilter.class);
    private WebFactory webFactory;
    private String mode;
    private String defaultRequestEncoding = "UTF-8";


    /**
     * Filter initialisation
     *
     * @see http://java.sun.com/j2ee/sdk_1.3/techdocs/api/javax/servlet/Filter.html#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
    	
    	String chibaConfigURI = "/idegaweb/bundles/org.chiba.web.bundle/resources/chiba-config.xml";
    	String styleSheetsPath = "/idegaweb/bundles/org.chiba.web.bundle/resources/xslt/";
    	
    	IWMainApplication iwma = IWMainApplication.getIWMainApplication(filterConfig.getServletContext());
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(iwma, chibaConfigURI);
    	
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(iwma, styleSheetsPath+"components.xsl");
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(iwma, styleSheetsPath+"html4.xsl");
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(iwma, styleSheetsPath+"ui.xsl");
    	IWBundleResourceFilter.checkCopyOfResourceToWebapp(iwma, styleSheetsPath+"html-form-controls.xsl");
    	
        mode = filterConfig.getInitParameter("scripted");
        webFactory = new WebFactory();
        webFactory.setServletContext(filterConfig.getServletContext());
        try {
            webFactory.initConfiguration();
            defaultRequestEncoding = webFactory.getConfig().getProperty("defaultRequestEncoding", defaultRequestEncoding);
            //webFactory.initLogging(this.getClass());
            webFactory.initTransformerService();
            webFactory.initXFormsSessionManager();
        } catch (XFormsConfigException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Filter shutdown
     *
     * @see http://java.sun.com/j2ee/sdk_1.3/techdocs/api/javax/servlet/Filter.html#destroy()
     */
    public void destroy() {
        if(LOG.isDebugEnabled()) {
            LOG.debug("cleanups allocated resources");
        }
        webFactory.destroyXFormsSessionManager();
    }


    /**
     * The actual filtering method
     *
     * @see http://java.sun.com/j2ee/sdk_1.3/techdocs/api/javax/servlet/Filter.html#doFilter(javax.servlet.ServletRequest,%20javax.servlet.ServletResponse,%20javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest srvRequest, ServletResponse srvResponse, FilterChain filterChain) throws IOException, ServletException {
        
    	//ensure correct Request encoding
    	if(srvRequest.getCharacterEncoding() == null)
    		srvRequest.setCharacterEncoding(defaultRequestEncoding);

    	
    	HttpServletRequest request = (HttpServletRequest) srvRequest;
        HttpServletResponse response = (HttpServletResponse) srvResponse;
        HttpSession session = request.getSession(true);

        /* before servlet request */
        if (isXFormUpdateRequest(request)) {
            LOG.info("Start Update XForm");

            try {
                XFormsSession xFormsSession = WebUtil.getXFormsSession(request, session);
                xFormsSession.setRequest(request);
                xFormsSession.setResponse(response);
                xFormsSession.handleRequest();
            } catch (XFormsException e) {
                throw new ServletException(e);
            }
            LOG.info("End Update XForm");
        } else {
        	

            //set mode of operation (scripted/nonscripted) by config
            if(this.mode == null)
            	this.mode = "true";
            
            if (this.mode.equalsIgnoreCase("true")){
                request.setAttribute(WebFactory.SCRIPTED,"true");
            }else if(this.mode.equalsIgnoreCase("false")){
                request.setAttribute(WebFactory.SCRIPTED,"false");
            }

            /* do servlet request */
            LOG.info("Passing to Chain");
            BufferedHttpServletResponseWrapper bufResponse = new BufferedHttpServletResponseWrapper((HttpServletResponse) srvResponse);
            filterChain.doFilter(srvRequest, bufResponse);
            LOG.info("Returned from Chain");


            /* dealing with response from chain */
            if(handleResponseBody(request, bufResponse)){
                byte[] data = prepareData(bufResponse);
                if(data.length > 0){
                    request.setAttribute(WebFactory.XFORMS_INPUTSTREAM,new ByteArrayInputStream(data));
                }
            }

            if(handleRequestAttributes(request)){
//                bufResponse.getOutputStream().close();
//                LOG.info("Start Filter XForm");
//
//                XFormsSession xFormsSession = null;
//                try {
//                    XFormsSessionManager sessionManager = DefaultXFormsSessionManagerImpl.getXFormsSessionManager();
//                    xFormsSession = sessionManager.createXFormsSession(request, response, session);
//                    xFormsSession.setBaseURI(request.getRequestURL().toString());
//                    xFormsSession.setXForms();
//                    xFormsSession.init();
//                    xFormsSession.handleRequest();
//                }
//                catch (Exception e) {
//                	LOG.error(e.getMessage(), e);
//                    if(xFormsSession != null){
//                        xFormsSession.close(e);
//                    }
//                }
//
//                LOG.info("End Render XForm");
            } else {
                srvResponse.getOutputStream().write(bufResponse.getData());
                srvResponse.getOutputStream().close();
            }
        }
    }

    protected byte[] prepareData(BufferedHttpServletResponseWrapper bufResponse) {
        //remove DOCTYPE PI if it exists, Xerces in Chiba otherwise may try to download the system DTD (can cause latency problems)
        byte[] data = removeDocumentTypePI(bufResponse.getData());
        //correct the <xforms:instance> xmlns="" problem (workaround for namespace problems in eXist)
        return correctInstanceXMLNS(data);
    }

    /**
     * returns true if one of XFORMS_NODE, XFORMS_URI, XFORMS_INPUTSOURCE or XFORMS_NPUTSTREAM constants is found
     * in the request.
     *
     * @param srvRequest the Servlet request
     * @return true if one of the XFORMS_* constants was found, otherwise false
     */
    protected boolean handleRequestAttributes(HttpServletRequest request) {
        if(request.getAttribute(WebFactory.XFORMS_NODE) != null
               || request.getAttribute(WebFactory.XFORMS_URI) != null
               || request.getAttribute(WebFactory.XFORMS_INPUTSOURCE) != null
               || request.getAttribute(WebFactory.XFORMS_INPUTSTREAM) != null){
            return true;
        }
        return false;
    }

    /**
     * Decides whether response body should be processed. For efficiency the parsing of the response body can be
     * turned off by setting chiba config property 'filter.ignoreResponseBody' to 'true'.<br/><br/>
     *
     * To overwrite the config setting (if set to 'true') on a per-request basis a request attribute of
     * PARSE_RESPONSE_BODY can be set.
     *
     * @param request HttpServletRequest
     * @param bufResponse The buffered response
     * @return true if the response contains an XForm, false otherwise
     */
    protected boolean handleResponseBody(HttpServletRequest request,BufferedHttpServletResponseWrapper bufResponse) {

        //[1] check if body parsing is explicitly requested
        if(request.getAttribute(WebFactory.PARSE_RESPONSE_BODY)!= null) return true;

        //[2] check if body parsing is explicitly unwanted
        if(request.getAttribute(WebFactory.IGNORE_RESPONSE_BODY)!= null) return false;

        //[3] check chiba config if response body parsing is disabled
        if (disableReponseBodyParsing()) return false;

        //[4] otherwise check response body for XForms markup
        String strResponse = bufResponse.getDataAsString();

        //find the xforms namespace local name
        int xfNSDeclEnd = strResponse.indexOf("=\"" + NamespaceConstants.XFORMS_NS + "\"");
        if (xfNSDeclEnd != -1) {
            String temp = strResponse.substring(0, xfNSDeclEnd);
            int xfNSDeclStart = temp.lastIndexOf(':') + 1;
            String xfNSLocal = temp.substring(xfNSDeclStart);

            //check for xforms model elements
            if (strResponse.contains('<' + xfNSLocal + ":model")) {
                return true;
            }
        }

        return false;
    }

    /**
     * turns on/off the parsing of the reponse body to determine the existence of some XForms markup in the response
     * stream. By default reponse body parsing is enabled unless it's turned of via configuration.
     *
     * @return true if configuration sets a value of 'true' for 'filter.ignoreResponseBody' in Chbia config and false
     * in all other cases
     */
    protected boolean disableReponseBodyParsing() {
        boolean ignoreResponse = false;
        try {
            ignoreResponse = Config.getInstance().getProperty(WebFactory.IGNORE_RESPONSEBODY).equalsIgnoreCase("true");
        } catch (XFormsConfigException e) {
            ignoreResponse = false; //default
        }
        if(ignoreResponse){
            return true;
        }
        return false;
    }

    /**
     * Checks if the request is to update an XForm
     *
     * @param srvRequest The request
     * @return true if the request is to update an XForm, false otherwise
     */
    public boolean isXFormUpdateRequest(HttpServletRequest srvRequest) {
        //get the http request object
        HttpServletRequest request = (HttpServletRequest) srvRequest;

        //must be a POST request
        if (!request.getMethod().equals("POST"))
            return false;

        String key = request.getParameter("sessionKey");
        if (key == null)
            return false;

        XFormsSessionManager manager = (XFormsSessionManager) request.getSession().getAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER);
        XFormsSession xFormsSession = manager.getXFormsSession(key);
        if (xFormsSession == null)
            return false;

        WebAdapter adapter = xFormsSession.getAdapter();
        if (adapter == null)
            return false;

        String actionURL;
        if (request.getQueryString() != null) {
            actionURL = request.getRequestURL() + "?" + request.getQueryString();
        } else {
            actionURL = request.getRequestURL().toString();
        }

        //remove the sessionKey (if any) before comparing the action URL
        int posSessionKey = actionURL.indexOf("sessionKey");
        if (posSessionKey > -1) {
            char preSep = actionURL.charAt(posSessionKey - 1);
            if (preSep == '?') {
                if (actionURL.indexOf('&') > -1) {
                    actionURL = actionURL.substring(0, posSessionKey) + actionURL.substring(actionURL.indexOf('&') + 1);
                } else {
                    actionURL = actionURL.substring(0, posSessionKey - 1);
                }
            } else if (preSep == '&') {
                actionURL = actionURL.substring(0, posSessionKey - 1);
            }
        }

        //if the action-url in the adapters context param is the same as that of the action url then we know we are updating
        return (adapter.getContextParam("action-url").equals(actionURL));
    }

    /**
     * Removes the DOCTYPE Processing Instruction from the content if it exists
     *
     * @param content The HTML page content
     * @return The content without the DOCTYPE PI
     */
    private byte[] removeDocumentTypePI(byte[] content) {
        String buf = new String(content);

        int iStartDoctype = buf.indexOf("<!DOCTYPE");
        if (iStartDoctype > -1) {
            int iEndDoctype = buf.indexOf('>', iStartDoctype);

            String newBuf = buf.substring(0, iStartDoctype - 1);
            newBuf += buf.substring(iEndDoctype + 1);
            return newBuf.getBytes();
        }
        return content;
    }

    /**
     * Inserts the attribute xmlns="" on the xforms:instance node if it is missing
     *
     * @param content The HTML page content
     * @return The content with the corrected xforms:instance
     */
    private byte[] correctInstanceXMLNS(byte[] content) {
        String buffer = new String(content);
        if (buffer.indexOf("<xforms:instance xmlns=\"\">") == -1) {
            String newBuf = buffer.replace("<xforms:instance>", "<xforms:instance xmlns=\"\">");
            return newBuf.getBytes();
        }

        return content;
    }

}
