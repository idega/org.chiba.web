package com.idega.chiba.web.session.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.myfaces.renderkit.html.util.HtmlBufferResponseWriterWrapper;
import org.chiba.adapter.ui.UIGenerator;
import org.chiba.adapter.ui.XSLTGenerator;
import org.chiba.web.IWBundleStarter;
import org.chiba.web.WebFactory;
import org.chiba.web.flux.IdegaFluxAdapter;
import org.chiba.web.servlet.ServletAdapter;
import org.chiba.web.servlet.WebUtil;
import org.chiba.web.session.XFormsSession;
import org.chiba.web.session.XFormsSessionManager;
import org.chiba.web.session.impl.DefaultXFormsSessionManagerImpl;
import org.chiba.web.session.impl.XFormsSessionBase;
import org.chiba.xml.events.XMLEvent;
import org.chiba.xml.xforms.config.XFormsConfigException;
import org.chiba.xml.xforms.exception.XFormsException;
import org.chiba.xml.xforms.ui.XformDocumentChange;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.idega.chiba.web.xml.xforms.bean.XformChangeBean;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.util.ListUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.RequestUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.xml.XmlUtil;

/**
 * Idega implementation of a XFormsSessionBase.
 *
 * @author Anton Makarov</a>
 */

public class IdegaXFormsSessionBase extends XFormsSessionBase {

	private static final AtomicLong SESSION_COUNTER = new AtomicLong();

	private String httpSessionId;
	private String originalKey;

	private Class<? extends HttpSession> httpSesionClass;

	private boolean finished;

	private long created;

	public IdegaXFormsSessionBase(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws XFormsException {
		super(request, response, new IdegaXFormHttpSession(session.getServletContext(), session.getId(), session.getCreationTime()));

		originalKey = key;
		//	Ensure uniqueness - System.CurrentTimeMills might not be unique for different threads.
		this.key = (this.getKey() + SESSION_COUNTER.incrementAndGet());

		httpSessionId = session.getId();
		httpSesionClass = httpSession.getClass();

		created = System.currentTimeMillis();
	}

	@Override
	protected void createAdapter() {
		this.adapter = isScripted() ? new IdegaFluxAdapter(key) /*new FluxAdapter()*/ : new ServletAdapter();
		adapter.setXFormsSession(this);
	}

	/**
     * processes the request after init.
     * @throws IOException
     * @throws XFormsException
     * @throws URISyntaxException
     */
    @Override
	public synchronized void handleRequest() throws XFormsException {
    	handleRequest(FacesContext.getCurrentInstance());
    }

    public synchronized void handleRequest(FacesContext context) throws XFormsException {
        boolean updating=false; //this will become true in case ServletAdapter is in use
        updateLRU();

        WebUtil.nonCachingResponse(response);

        try {
            XMLEvent exitEvent = adapter.checkForExitEvent();
            if (exitEvent == null) {
                String referer = null;

                if (updating) {
                    // updating ... - this is only called when ServletAdapter is in use
                    referer = (String) getProperty(XFormsSession.REFERER);
                    setProperty("update","true");
                    String forwardTo = request.getContextPath() + "/view?"+IWBundleStarter.SESSION_KEY+"=" + getKey() + "&referer=" + referer;
                    response.sendRedirect(response.encodeRedirectURL(forwardTo));
                } else {
                    referer = request.getQueryString();

                    response.setContentType(WebUtil.HTML_CONTENT_TYPE);
                    //we got an initialization request (GET) - the session is not registered yet
                    UIGenerator uiGenerator = createUIGenerator(context);
                    //store UIGenerator in this session as a property
                    setProperty(XFormsSession.UIGENERATOR, uiGenerator);
                    //store queryString as 'referer' in XFormsSession
                    setProperty(XFormsSession.REFERER, request.getContextPath() + request.getServletPath() + "?" + referer);
                    //actually register the XFormsSession with the manager
                    getManager().addXFormsSession(this);

                    /* The XFormsSessionManager is kept in the http-session though it is accessible as singleton. Subsequent
                    servlets should access the manager through the http-session attribute as below to ensure the http-session
                    is refreshed.*/
                    Object xformsManager = httpSession.getAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER);
                    if (!(xformsManager instanceof XFormsSessionManager)) {
                    	//	Making sure manager is set to the HTTP session
                    	httpSession.setAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER, IdegaXFormSessionManagerImpl.getXFormsSessionManager());
                    }

                    //	Converting XForm to the HTML
                    uiGenerator.setInput(getAdapter().getXForms());
                    writeXform(uiGenerator, context);


                }
            } else {
            	handleExit(exitEvent);
            }
        } catch (IOException e) {
            throw new XFormsException(e);
        } catch (URISyntaxException e) {
            throw new XFormsException(e);
        }
    }

    protected Logger getLogger(){
    	return Logger.getLogger(getClass().getName());
    }
    private void writeXform(UIGenerator uiGenerator,FacesContext context) throws XFormsException, IOException{
    	XformChangeBean xformChangeBean = ELUtil.getInstance().getBean(XformChangeBean.BEAN_NAME);
    	List<XformDocumentChange> changeBeans = xformChangeBean.getXformDocumentChangeBeans();
    	if(ListUtil.isEmpty(changeBeans)){
    		generateUi(uiGenerator,context.getResponseWriter());
    		return;
    	}
    	//TODO think of other way...
    	IWContext iwc = IWContext.getIWContext(context);
    	org.w3c.dom.Document w3Document = (org.w3c.dom.Document) getAdapter().getXForms();
    	NodeList nodelist = w3Document.getElementsByTagName("link");
    	int size = nodelist.getLength();
    	for(int i = 0;i < size;i++){
    		try{
	    		Node item = nodelist.item(i);
	    		Node href = item.getAttributes().getNamedItem("href");
	    		String uri =  href.getNodeValue();
	    		PresentationUtil.addStyleSheetToHeader(iwc,uri);
    		}catch (Exception e) {
    			break;
			}
    	}
    	nodelist = w3Document.getElementsByTagName("script");
    	size = nodelist.getLength();
    	for(int i = 0;i < size;i++){
    		try{
	    		Node item = nodelist.item(i);
	    		Node href = item.getAttributes().getNamedItem("src");
	    		String uri =  href.getNodeValue();
	    		PresentationUtil.addJavaScriptSourceLineToHeader(iwc, uri);
    		}catch (Exception e) {
				break;
			}
    	}
    	//---------------
    	HtmlBufferResponseWriterWrapper writer = null;
		ResponseWriter responseWriter = context.getResponseWriter();
		writer = HtmlBufferResponseWriterWrapper.getInstance(responseWriter);
		generateUi(uiGenerator,writer);
    	String rendered = writer.toString();
    	Document document = XmlUtil.getJDOMXMLDocument(rendered, false);
    	for(XformDocumentChange changeBean : changeBeans){
    		document = changeBean.getChangedDocument(document);
    	}
		Format format = Format.getPrettyFormat();
		format.setExpandEmptyElements(true);
    	XMLOutputter outputter = new XMLOutputter(format);
    	outputter.output(document, responseWriter);
    }

    private void generateUi(UIGenerator uiGenerator,ResponseWriter writer) throws XFormsException{
    	uiGenerator.setOutput(writer);
    	uiGenerator.generate();
    }

    @Override
	protected UIGenerator createUIGenerator() throws URISyntaxException, XFormsException {
    	return createUIGenerator(FacesContext.getCurrentInstance());
    }

    protected UIGenerator createUIGenerator(FacesContext context) throws URISyntaxException, XFormsException {
    	IWContext iwc = IWContext.getIWContext(context);
    	if (iwc.getRequest().getAttribute(WebFactory.SCRIPTED) == null)
    		iwc.getRequest().setAttribute(WebFactory.SCRIPTED, Boolean.TRUE.toString());

    	XSLTGenerator generator = (XSLTGenerator) super.createUIGenerator();

    	IWBundle bundle = IWMainApplication.getDefaultIWMainApplication().getBundle(IWBundleStarter.BUNDLE_IDENTIFIER);
		generator.setParameter("scriptPath", bundle.getVirtualPathWithFileNameString("javascript/"));
		generator.setParameter("imagesPath", bundle.getVirtualPathWithFileNameString("style/images/"));

		try {
			IWResourceBundle iwrb = bundle.getResourceBundle(iwc);

			generator.setParameter("standardLayerMsg", iwrb.getLocalizedString("chiba.standard_layer_message", "Processing data..."));
			generator.setParameter("loadingLayerMsg", iwrb.getLocalizedString("chiba.loading_layer_message", "Loading..."));
			generator.setParameter("reloadPageBecauseOfErrorMsg", iwrb.getLocalizedString("chiba.reload_page_because_of_error_message",
																"Unfortunately the page was not loaded correctly. Please click OK to reload it."));
			generator.setParameter("sessionExpiredMsg", iwrb.getLocalizedString("chiba.session_expired_messsage", "Your session has expired. Please try again."));
			generator.setParameter("downloadingPDFForXFormMsg", iwrb.getLocalizedString("chiba.downloading_pdf_for_xform", "Downloading PDF"));
			generator.setParameter("uploadingFailed", iwrb.getLocalizedString("chiba.uploading_failed", "Sorry, uploading failed. Please try again."));
			generator.setParameter("invalidFileProvidedToUpload", iwrb.getLocalizedString("chiba.invalid_file_to_upload", "Invalid file provided to upload!"));
			generator.setParameter("closingMsg", iwrb.getLocalizedString("chiba.closing", "Closing..."));
			generator.setParameter("errorSavingForm", iwrb.getLocalizedString("chiba.unable_to_save_form", "Unable to save data. Please re-fill form with data"));
			generator.setParameter("continueOrStopFillingForm", iwrb.getLocalizedString("chiba.continue_or_stop_filling_form",
					"The form was successfully saved. Do you want to continue filling the form?"));
			generator.setParameter("userMustBeLoggedIn",
					iwrb.getLocalizedString("chiba.user_must_be_logged_in", "Your session has expired, you must to login to continue your work"));

			generator.setParameter(RequestUtil.HEADER_USER_AGENT.toLowerCase(), iwc.getUserAgent());
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return generator;
    }

    @Override
	protected void initChibaConfig() throws XFormsException {
		//Configuration is done once on application startup.
    	//No need to do it for each session.
	}

	/**
     * returns the XFormsSessionManager used
     * @return the XFormsSessionManager used
     */
    @Override
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

	public String getHttpSessionId() {
		return httpSessionId;
	}

	public String getOriginalKey() {
		return originalKey;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Class<? extends HttpSession> getSessionClass() {
		return httpSesionClass;
	}

	public long getCreatedTimestamp() {
		return created;
	}

	@Override
	public String toString() {
		return "XForm session. Key: ".concat(getKey()).concat(" for HTTP session: ").concat(getHttpSessionId() + ". Ready to be deleted: " + isFinished() +
				". Created: " + new Date(created) + ". Last time used: " + (getLastUseTime() < 0 ? "unknown" : new Date(getLastUseTime())));
	}
}