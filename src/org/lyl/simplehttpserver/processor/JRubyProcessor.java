package org.lyl.simplehttpserver.processor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.lyl.simplehttpserver.core.Request;
import org.lyl.simplehttpserver.core.Response;

public class JRubyProcessor extends AbstractScriptProcessor {
	
	public JRubyProcessor(){
		this("");
	}
	
	public JRubyProcessor(String jrubyhome) {
		System.setProperty("jruby.home", jrubyhome);
	}
	
	@Override
	public void runScript(File fp, Request req, Response resp)
			throws IOException, ScriptException, NoSuchMethodException {
		JRubyResponse jresp = new JRubyResponse(resp);
		String function_name = "do_" + req.getType().toLowerCase();
		String key = fp.getPath() + fp.lastModified();
		ScriptEngine engine = (ScriptEngine)cache.retive(key);
		
		if(engine == null) {
			engine = manager.getEngineByName("jruby");
        	FileReader file = new FileReader(fp);
        	engine.eval(file);
        	file.close();
        	cache.cache(key, engine);
		}
		
        Invocable inv = (Invocable) engine;
        inv.invokeFunction(function_name, req, jresp);
        jresp.flush();
	}

	private static class JRubyResponse extends Response {
		private StringWriter buffer = new StringWriter();
		private PrintWriter writer = new PrintWriter(buffer);
		private Response _resp;

		JRubyResponse(Response resp) throws IOException {
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
