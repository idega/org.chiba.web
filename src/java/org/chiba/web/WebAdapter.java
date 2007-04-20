// Copyright 2001-2007 ChibaXForms GmbH
/*
 *
 * Artistic License
 *
 * Preamble
 *
 * The intent of this document is to state the conditions under which a
 * Package may be copied, such that the Copyright Holder maintains some
 * semblance of artistic control over the development of the package, while
 * giving the users of the package the right to use and distribute the
 * Package in a more-or-less customary fashion, plus the right to make
 * reasonable modifications.
 *
 * Definitions:
 *
 * "Package" refers to the collection of files distributed by the Copyright
 * Holder, and derivatives of that collection of files created through
 * textual modification.
 *
 * "Standard Version" refers to such a Package if it has not been modified,
 * or has been modified in accordance with the wishes of the Copyright
 * Holder.
 *
 * "Copyright Holder" is whoever is named in the copyright or copyrights
 * for the package.
 *
 * "You" is you, if you're thinking about copying or distributing this
 * Package.
 *
 * "Reasonable copying fee" is whatever you can justify on the basis of
 * media cost, duplication charges, time of people involved, and so
 * on. (You will not be required to justify it to the Copyright Holder, but
 * only to the computing community at large as a market that must bear the
 * fee.)
 *
 * "Freely Available" means that no fee is charged for the item itself,
 * though there may be fees involved in handling the item. It also means
 * that recipients of the item may redistribute it under the same
 * conditions they received it.
 *
 * 1. You may make and give away verbatim copies of the source form of the
 *    Standard Version of this Package without restriction, provided that
 *    you duplicate all of the original copyright notices and associated
 *    disclaimers.
 *
 * 2. You may apply bug fixes, portability fixes and other modifications
 *    derived from the Public Domain or from the Copyright Holder. A
 *    Package modified in such a way shall still be considered the Standard
 *    Version.
 *
 * 3. You may otherwise modify your copy of this Package in any way,
 *    provided that you insert a prominent notice in each changed file
 *    stating how and when you changed that file, and provided that you do
 *    at least ONE of the following:
 *
 *    a) place your modifications in the Public Domain or otherwise make
 *    them Freely Available, such as by posting said modifications to
 *    Usenet * or an equivalent medium, or placing the modifications on a
 *    major * archive site such as ftp.uu.net, or by allowing the Copyright
 *    Holder * to include your modifications in the Standard Version of the
 *    Package.
 *
 *    b) use the modified Package only within your corporation or *
 *    organization.
 *
 *    c) rename any non-standard executables so the names do not conflict
 *    with standard executables, which must also be provided, and provide a
 *    separate manual page for each non-standard executable that clearly
 *    documents how it differs from the Standard Version.
 *
 *    d) make other distribution arrangements with the Copyright Holder.
 *
 * 4. You may distribute the programs of this Package in object code or
 *    executable form, provided that you do at least ONE of the following:
 *
 *    a) distribute a Standard Version of the executables and library
 *    files, together with instructions (in the manual page or equivalent)
 *    on where to get the Standard Version.
 *
 *    b) accompany the distribution with the machine-readable source of the
 *    Package with your modifications.
 *
 *    c) accompany any non-standard executables with their corresponding
 *    Standard Version executables, giving the non-standard executables
 *    non-standard names, and clearly documenting the differences in manual
 *    pages (or equivalent), together with instructions on where to get the
 *    Standard Version.
 *
 *    d) make other distribution arrangements with the Copyright Holder.
 *
 * 5. You may charge a reasonable copying fee for any distribution of this
 *    Package. You may charge any fee you choose for support of this
 *    Package. You may not charge a fee for this Package itself.  However,
 *    you may distribute this Package in aggregate with other (possibly
 *    commercial) programs as part of a larger (possibly commercial)
 *    software distribution provided that you do not advertise this Package
 *    as a product of your own.
 *
 * 6. The scripts and library files supplied as input to or produced as
 *    output from the programs of this Package do not automatically fall
 *    under * the copyright of this Package, but belong to whomever
 *    generated them, * and may be sold commercially, and may be aggregated
 *    with this Package.
 *
 * 7. C or perl subroutines supplied by you and linked into this Package
 *    shall not be considered part of this Package.
 *
 * 8. The name of the Copyright Holder may not be used to endorse or
 *    promote products derived from this software without specific prior *
 *    written permission.
 *
 * 9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 */

package org.chiba.web;

import org.apache.log4j.Logger;
import org.chiba.adapter.AbstractChibaAdapter;
import org.chiba.adapter.ChibaEvent;
import org.chiba.web.flux.FluxAdapter;
import org.chiba.web.servlet.HttpRequestHandler;
import org.chiba.web.session.XFormsSession;
import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.events.ChibaEventNames;
import org.chiba.xml.events.XMLEvent;
import org.chiba.xml.ns.NamespaceConstants;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import javax.xml.transform.TransformerException;

/**
 * Superclass for Adapters used in web applications. Does minimal event listening on the processor and provides
 * a common base to build webadapers.
 *
 * @author Joern Turner
 * @version $Id: WebAdapter.java,v 1.2 2007/04/20 18:40:16 civilis Exp $
 * @see org.chiba.web.flux.FluxAdapter
 * @see org.chiba.web.servlet.ServletAdapter
 *
 */
public class WebAdapter extends AbstractChibaAdapter implements EventListener {
    /**
     * Defines the key for accessing (HTTP) session ids.
     */
    public static final String SESSION_ID = "chiba.session.id";

    private static final Logger LOGGER = Logger.getLogger(FluxAdapter.class);
    protected EventTarget root;
    protected XFormsSession xformsSession;
    protected HttpRequestHandler httpRequestHandler;
    protected XMLEvent exitEvent = null;
    public static final String USERAGENT = "useragent";
    public static final String REQUEST_URI = "requestURI";

    public WebAdapter() {
        this.chibaBean = createXFormsProcessor();
    }


    public void setXFormsSession(XFormsSession xFormsSession) {
        this.xformsSession = xFormsSession;
    }

// --Commented out by Inspection START (3/28/07 11:47 AM):
//    public void setExitEvent(XMLEvent event){
//        this.exitEvent = event;
//    }
// --Commented out by Inspection STOP (3/28/07 11:47 AM)

    /**
     * initialize the Adapter. This is necessary cause often the using
     * application will need to configure the Adapter before actually using it.
     *
     * @throws org.chiba.xml.xforms.exception.XFormsException
     */
    public void init() throws XFormsException {
        // get docuent root as event target in order to capture all events
        this.root = (EventTarget) this.chibaBean.getXMLContainer().getDocumentElement();

        // interaction events my occur during init so we have to register before
        this.root.addEventListener(ChibaEventNames.LOAD_URI, this, true);
        this.root.addEventListener(ChibaEventNames.RENDER_MESSAGE, this, true);
        this.root.addEventListener(ChibaEventNames.REPLACE_ALL, this, true);

        configureSession();
        
        // init processor
        this.chibaBean.init();

    }

    private void configureSession() throws XFormsException {
        Document hostDocument = this.chibaBean.getXMLContainer();
        Element root = hostDocument.getDocumentElement();
        Element keepAlive = DOMUtil.findFirstChildNS(root, NamespaceConstants.CHIBA_NS,"keepalive");
        if(keepAlive != null){
            String pulse = keepAlive.getAttributeNS(null,"pulse");
            if(!(pulse == null || pulse.equals(""))){
                xformsSession.setProperty(XFormsSession.KEEPALIVE_PULSE,pulse);
            }
        }
    }

    public XMLEvent checkForExitEvent() {
        return this.exitEvent;
    }

    /**
     * Dispatch a ChibaEvent to trigger some XForms processing such as updating
     * of values or execution of triggers.
     *
     * @param event an application specific event
     * @throws org.chiba.xml.xforms.exception.XFormsException
     * @see org.chiba.adapter.DefaultChibaEventImpl
     */
    public void dispatch(ChibaEvent event) throws XFormsException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Event " + event.getEventName() + " dispatched");
            LOGGER.debug("Event target: " + event.getId());
            try {
                if(this.chibaBean != null){
                    DOMUtil.prettyPrintDOM(this.chibaBean.getXMLContainer(),System.out);
                }
            } catch (TransformerException e) {
                throw new XFormsException(e);
            }
        }

    }

    /**
     * listen to processor and add a DefaultChibaEventImpl object to the
     * EventQueue.
     *
     * @param event the handled DOMEvent
     */
    public void handleEvent(Event event) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("handleEvent: " + event.getType());
        }
    }

    /**
     * terminates the XForms processing. right place to do cleanup of
     * resources.
     *
     * @throws org.chiba.xml.xforms.exception.XFormsException
     */
    public void shutdown() throws XFormsException {
        // shutdown processor
        if(this.chibaBean != null){
            this.chibaBean.shutdown();
            this.chibaBean = null;
        }

        // deregister for interaction events if any
        if(this.root != null){
            this.root.removeEventListener(ChibaEventNames.LOAD_URI, this, true);
            this.root.removeEventListener(ChibaEventNames.RENDER_MESSAGE, this, true);
            this.root.removeEventListener(ChibaEventNames.REPLACE_ALL, this, true);
            this.root = null;
        }
    }

    protected HttpRequestHandler getHttpRequestHandler() {
        if (this.httpRequestHandler == null) {
            this.httpRequestHandler = new HttpRequestHandler(this.chibaBean);
            this.httpRequestHandler.setUploadRoot(this.uploadDestination);
            this.httpRequestHandler.setSessionKey(this.xformsSession.getKey());
        }

        return this.httpRequestHandler;
    }
}