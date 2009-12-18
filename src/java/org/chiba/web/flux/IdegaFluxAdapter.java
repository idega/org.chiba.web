package org.chiba.web.flux;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.chiba.adapter.ChibaEvent;
import org.chiba.web.servlet.HttpRequestHandler;
import org.chiba.xml.xforms.ChibaBean;
import org.chiba.xml.xforms.IdegaChibaBean;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.xforms.XFormsModelElement;
import org.xml.sax.InputSource;

import com.idega.chiba.web.session.impl.IdegaXFormSessionManagerImpl;
import com.idega.core.cache.IWCacheManager2;
import com.idega.idegaweb.IWMainApplication;

public class IdegaFluxAdapter extends FluxAdapter {

	private static final Logger LOGGER = Logger.getLogger(IdegaFluxAdapter.class.getName());
	
	private static final long CACHE_IDLE_TIME = 2 * 60 * 60;	//	2 hours by default
	public static final String CHIBA_BEANS_CACHE_NAME = "ChibaBeansCache";
	
	private String key;
	
	public IdegaFluxAdapter(String key) {
		super();
		
		this.key = key;
		putChibaBean();
	}
	
	//	TODO: finish up IdegaChibaBean!
	@Override
	protected ChibaBean createXFormsProcessor() {
		return new IdegaChibaBean();
	}

	@Override
	public void init() throws XFormsException {
		chibaBean = getChibaBean();
		
		super.init();
		
		putChibaBean();
    }
    
    @Override
	public void dispatch(ChibaEvent event) throws XFormsException {
    	chibaBean = getChibaBean();

    	super.dispatch(event);
    	
    	putChibaBean();
    }

    @Override
	public void handleEvent(Event event) {
    	chibaBean = getChibaBean();

    	super.handleEvent(event);
    	
    	putChibaBean();
    }

    @Override
	public void setXForms(Node node) throws XFormsException {
    	chibaBean = getChibaBean();
    	
    	super.setXForms(node);
    	
    	putChibaBean();
    }

    @Override
	public void setXForms(URI uri) throws XFormsException {
    	chibaBean = getChibaBean();
    	
    	super.setXForms(uri);
    	
    	putChibaBean();
    }

    @Override
	public void setXForms(InputStream stream) throws XFormsException {
    	chibaBean = getChibaBean();
    	
    	super.setXForms(stream);
    	
    	putChibaBean();
    }

    @Override
	public void setXForms(InputSource source) throws XFormsException {
    	chibaBean = getChibaBean();
    	
    	super.setXForms(source);
    	
    	putChibaBean();
    }

    @Override
	public void setBaseURI(String aURI) {
    	chibaBean = getChibaBean();
    	
    	super.setBaseURI(aURI);
    	
    	putChibaBean();
    }

    @Override
	public void setConfigPath(String path) throws XFormsException {
    	chibaBean = getChibaBean();
    	
    	super.setConfigPath(path);
    	
    	putChibaBean();
    }

    @SuppressWarnings("unchecked")
	@Override
	public void setContext(Map contextParams) {
    	chibaBean = getChibaBean();
    	
    	super.setContext(contextParams);
    	
    	putChibaBean();
    }

	@Override
	public void setContextParam(String key, Object object) {
    	chibaBean = getChibaBean();
    	
    	super.setContextParam(key, object);
    	
    	putChibaBean();
    }

    @Override
	public Object getContextParam(String key) {
    	chibaBean = getChibaBean();
    	
    	try {
    		return super.getContextParam(key);
    	} finally {
    		putChibaBean();
    	}
    }

    @Override
	public Object removeContextParam(String key) {
    	chibaBean = getChibaBean();
    	
    	try {
    		return super.removeContextParam(key);
    	} finally {
    		putChibaBean();
    	}
    }

    @Override
	public Node getXForms() throws XFormsException {
    	chibaBean = getChibaBean();
    	
    	try {
    		return super.getXForms();
    	} finally {
    		putChibaBean();
    	}
    }

    @Override
	public XFormsModelElement getXFormsModel(String id) throws XFormsException {
    	chibaBean = getChibaBean();
    	
    	try {
    		return super.getXFormsModel(id);
    	} finally {
    		putChibaBean();
    	}
    }

    @Override
	public void shutdown() throws XFormsException {
    	chibaBean = getChibaBean();
    	
    	try {
    		super.shutdown();
    	} catch (Exception e) {
    		LOGGER.log(Level.WARNING, "Error shutting down adapter using key: " + key, e);
    	} finally {
    		removeChibaBean();
    	}
    }

    @Override
	protected HttpRequestHandler getHttpRequestHandler() {
    	chibaBean = getChibaBean();
    	
    	try {
    		return super.getHttpRequestHandler();
    	} finally {
    		putChibaBean();
    	}
    }
	
    private void removeChibaBean() {
    	getChibaBeansCache().remove(key);
    	chibaBean = null;
    }
    
	private ChibaBean getChibaBean() {
		return getChibaBeansCache().get(key);
	}
	
	private void putChibaBean() {
		getChibaBeansCache().put(key, (IdegaChibaBean) chibaBean);
		chibaBean = null;
	}
	
	private Map<String, IdegaChibaBean> getChibaBeansCache() {
		IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		return IWCacheManager2.getInstance(iwma).getCache(CHIBA_BEANS_CACHE_NAME, IdegaXFormSessionManagerImpl.getMaxSessions(), true, false, CACHE_IDLE_TIME,
				CACHE_IDLE_TIME);
	}
}