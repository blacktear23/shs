package org.lyl.simplehttpserver.processor;

import org.lyl.simplehttpserver.cache.Cache;
import org.lyl.simplehttpserver.cache.CacheEngine;
import org.lyl.simplehttpserver.core.MimeType;
import org.lyl.simplehttpserver.services.HttpConnectionService;

public abstract class AbstractRequestProcessor implements RequestProcessor {
	private static final MimeType mime = MimeType.getMimeType();
	private HttpConnectionService service;
	private static final Cache cache = CacheEngine.getCache();
	
	public static Cache getCache() {
		return cache;
	}


	public HttpConnectionService getService() {
		return service;
	}


	public void setService(HttpConnectionService service) {
		this.service = service;
	}


	public MimeType getMimeType() {
		return mime;
	}
}
