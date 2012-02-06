package org.lyl.simplehttpserver.processor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.lyl.simplehttpserver.core.Request;
import org.lyl.simplehttpserver.core.Response;

public class JScriptProcessor extends AbstractScriptProcessor {
	private static Logger log = LogManager.getLogger(JScriptProcessor.class);

	public JScriptProcessor() {
		log.info("New JScriptProcessor");
	}
	
	public void runScript(File fp, Request req, Response resp) throws IOException, ScriptException, NoSuchMethodException {
		JSResponse jresp = new JSResponse(resp);
		if(fp.length() > Integer.MAX_VALUE) {
			log.error("User request a too big script file.");
		}
		String function_name = "do_" + req.getType().toLowerCase();

		ScriptEngine engine = manager.getEngineByName("JavaScript");
    	FileReader file = new FileReader(fp);
    	engine.eval(file);
    	file.close();
        Invocable inv = (Invocable) engine;
        inv.invokeFunction(function_name, req, jresp);
        jresp.flush();
	}
	
	private static class JSResponse extends Response {
		private StringWriter buffer = new StringWriter();
		private PrintWriter writer = new PrintWriter(buffer);
		private Response _resp;
		
		JSResponse(Response resp) throws IOException {
			super(resp.getSocket());
			this._resp = resp;
		}
		
		@Override
		public PrintWriter getPrintWriter() {
			return this.writer;
		}
		
		@Override
		public void setHeader(String key, String value) {
			_resp.setHeader(key, value);
		}
		
		@Override
		public String getHeader(String key) {
			return _resp.getHeader(key);
		}
		
		@Override
		public void setStatuCode(int code) {
			_resp.setStatuCode(code);
		}
		
		@Override
		public int getStatuCode() {
			return _resp.getStatuCode();
		}
		
		@Override
		public void setMimeType(String type) {
			_resp.setMimeType(type);
		}
		
		@Override
		public void flush() throws IOException {
			String res = buffer.toString();
			_resp.setHeader("Content-Length", Integer.toString(res.getBytes(getEncoding()).length));
			_resp.getPrintWriter().write(_resp.encodeHeader());
			_resp.getPrintWriter().write(res);
			super.flush();
		}
	}
}
