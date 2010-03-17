package com.idega.chiba.cache;

import java.util.logging.Logger;

import org.chiba.web.session.XFormsSession;

import com.idega.core.cache.CacheMapListener;

public class XFormsSessionsCacheListener<K, V extends XFormsSession> implements CacheMapListener<String, V> {

	private static final Logger LOGGER = Logger.getLogger(XFormsSessionsCacheListener.class.getName());
	
	public void cleared() {
	}

	public void gotObject(String key, V object) {
	}

	public void putObject(String key, V object) {
	}

	public void removedObject(String key) {
		LOGGER.info("Just removed XForms session: " + key);
	}

}