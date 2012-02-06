package org.lyl.simplehttpserver.processor;

import java.io.File;
import java.io.IOException;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.lyl.simplehttpserver.cache.Cache;
import org.lyl.simplehttpserver.cache.CacheEngine;
import org.lyl.simplehttpserver.core.Request;
import org.lyl.simplehttpserver.core.Response;

public abstract class AbstractScriptProcessor extends AbstractFileProcessor {
	protected static ScriptEngineManager manager = new ScriptEngineManager();
	protected Cache cache = CacheEngine.getCache();
	
	public void processRequest(Request req, Response resp) throws Exception {
		int statu = getResponseStatuCode(req.getPath());
		File fp = getFile(req.getPath());
		if(statu == 200 && fp.isFile()) {
			resp.setStatuCode(statu);
			resp.setHeader("Content-Type", "text/html; charset=" + resp.getEncoding());
			runScript(fp, req, resp);
		} else {
			resp.setStatuCode(404);
			ErrorProcessor err = new ErrorProcessor();
			err.setService(getService());
			err.processRequest(req, resp);
		}		
	}
	
	public abstract void runScript(File fp, Request req, Response resp) throws IOException, ScriptException, NoSuchMethodException;
}
