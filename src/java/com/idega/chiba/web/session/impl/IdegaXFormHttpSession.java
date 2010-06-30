package com.idega.chiba.web.session.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.idega.util.ArrayUtil;
import com.idega.util.CoreConstants;

@SuppressWarnings("deprecation")
public class IdegaXFormHttpSession implements HttpSession, Serializable {

	private static final long serialVersionUID = -4600644491379371262L;
	private static final Logger LOGGER = Logger.getLogger(IdegaXFormHttpSession.class.getName());
	
	private int maxInactiveInterval;
	
	private long creationTime;
	
	private ServletContext servletContext;
	
	private String id;
	
	private Map<String, Object> values;
	private Map<String, Object> attributes;
	
	public IdegaXFormHttpSession(ServletContext servletContext, String id, long creationTime) {
		this.servletContext = servletContext;
		this.id = id.concat(CoreConstants.UNDER).concat(UUID.randomUUID().toString());
		this.creationTime = creationTime;
		
		values = new HashMap<String, Object>();
		attributes = new HashMap<String, Object>();
	}
	
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(attributes.keySet());
	}

	public long getCreationTime() {
		return creationTime;
	}

	public String getId() {
		return id;
	}

	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public HttpSessionContext getSessionContext() {
		LOGGER.warning("This method is not implemented!");
		return null;
	}

	public Object getValue(String key) {
		return values.get(key);
	}

	public String[] getValueNames() {
		return ArrayUtil.convertListToArray(values.keySet());
	}

	public void invalidate() {
		LOGGER.warning("This method is not implemented!");
	}

	public boolean isNew() {
		LOGGER.warning("This method is not implemented!");
		return false;
	}

	public void putValue(String key, Object value) {
		values.put(key, value);
	}

	public void removeAttribute(String key) {
		attributes.remove(key);
	}

	public void removeValue(String key) {
		values.remove(key);
	}

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

}