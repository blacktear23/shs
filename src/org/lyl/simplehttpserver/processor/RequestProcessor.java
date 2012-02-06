package org.lyl.simplehttpserver.processor;

import org.lyl.simplehttpserver.core.Request;
import org.lyl.simplehttpserver.core.Response;
import org.lyl.simplehttpserver.services.HttpConnectionService;


public interface RequestProcessor {
	void processRequest(Request req, Response resp) throws Exception;
	void setService(HttpConnectionService service);
	HttpConnectionService getService();
}
