package com.idega.chiba.cache;

import javax.servlet.http.HttpSession;

import org.chiba.web.session.XFormsSession;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.chiba.web.session.impl.IdegaXFormsSessionBase;
import com.idega.core.business.DefaultSpringBean;
import com.idega.core.cache.CacheMapGuardian;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.CoreUtil;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class XFormsSessionsCacheGuardian<K, V extends XFormsSession> extends DefaultSpringBean implements CacheMapGuardian<String, V> {

	public boolean beforeClear() {
		return Boolean.FALSE;
	}

	public boolean beforeGet(String key) {
		return Boolean.TRUE;
	}

	public boolean beforePut(String key, V object) {
		return object instanceof IdegaXFormsSessionBase;
	}

	public boolean beforeRemove(String key, V object) {
		IdegaXFormsSessionBase session = null;
		if (object instanceof IdegaXFormsSessionBase) {
			session = (IdegaXFormsSessionBase) object;
		}
		if (session == null) {
			getLogger().warning("XForm session " + object + " with a key " + key + " is not of expected type! Can not guard it!");
			return Boolean.TRUE;
		}
		
		if (session.isFinished()) {
			getLogger().info("Allowing to remove: " + session);
			return Boolean.TRUE;
		}
		
		HttpSession httpSession = getSession();
		if (httpSession == null) {
			getLogger().warning("HTTP session for " + session + " was not found, but not allowing to remove it.");
			return Boolean.FALSE;
		}
		
		String httpIdFromHttp = httpSession.getId();
		String httpIdFromXFormSession = session.getHttpSessionId();
		if (!httpIdFromHttp.equals(httpIdFromXFormSession)) {
			getLogger().warning("IDs of HTTP session do not match (from HTTP session object: " + httpIdFromHttp + ", from XForm session: " +
					httpIdFromXFormSession + ")! Not allowing to remove " + session);
			return Boolean.FALSE;
		}
		
		long lastAccessed = httpSession.getLastAccessedTime();
		long now = System.currentTimeMillis();
		Long idleTime = Long.valueOf(now - lastAccessed) / 1000;
		if (idleTime.intValue() > httpSession.getMaxInactiveInterval()) {
			getLogger().warning("HTTP session " + httpIdFromHttp + " was inactive for " + idleTime + " seconds (more than allowed), but " + session +
					" will not be removed.");
			return Boolean.FALSE;	//	Not letting to remove XForm session that was not finished even if HTTP session was invalidated.
		}
		
		getLogger().info("Nothing bad diagnosted about " + session + ", not allowing to remove!");
		if (IWMainApplication.getDefaultIWMainApplication().getSettings().getBoolean("log_xform_guardian", Boolean.FALSE)) {
			try {
				throw new RuntimeException("Attempted to remove " + session);
			} catch (Exception e) {
				CoreUtil.sendExceptionNotification("Checking stack trace of an attempt to remove XForm session from a cache", e);
			}
		}
		return Boolean.FALSE;
	}

}