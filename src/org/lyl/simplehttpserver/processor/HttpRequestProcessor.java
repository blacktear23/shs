package org.lyl.simplehttpserver.processor;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.lyl.simplehttpserver.core.Request;
import org.lyl.simplehttpserver.core.Response;
import sun.misc.BASE64Decoder;

public class HttpRequestProcessor extends AbstractFileProcessor {
	private static Logger log = LogManager.getLogger(HttpRequestProcessor.class);
	
	public HttpRequestProcessor() {
		log.debug("New HttpRequestProcessor");
	}
	private int bufferSize = 1024;
	
	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	private String username = "";

	public void setUsername(String value) {
		this.username = value;
	}

	private String password = "";

	public void setPassword(String value) {
		this.password = value;
	}

	private String realm = "SimpleHttpServer";

	public void setRealm(String value) {
		this.realm = value;
	}

	private boolean enableHttpBasicAuth = false;

	public void setEnableHttpBasicAuth(boolean value) {
		this.enableHttpBasicAuth = value;
	}

	private boolean validateHttpBasicAuth(Request req) throws IOException {
		if(!req.containsHeader("Authorization"))
			return false;
		String basicAuthString = req.getHeader("Authorization");
		String[] items = basicAuthString.split(" ");
		if(items.length != 2 || !items[0].equals("Basic"))
			return false;
		BASE64Decoder decoder = new BASE64Decoder();
		String authString = new String(decoder.decodeBuffer(items[1]));
		if(authString.equals(this.username + ":" + this.password))
			return true;
		else
			return false;
	}

	private void sendUnauthorizedResponse(Response resp) throws IOException {
		resp.setStatuCode(401);
		resp.setHeader("WWW-Authenticate", "Basic realm=\"" + this.realm + "\"");
		OutputStream os = resp.getOutputStream();
		os.write(resp.encodeHeader().getBytes());
		os.write("HTTP Basic: Access denied.\n".getBytes());
		os.flush();
	}

	public void processRequest(Request req, Response resp) throws Exception {
		if(enableHttpBasicAuth) {
			if(!validateHttpBasicAuth(req)) {
				sendUnauthorizedResponse(resp);
				return;
			}
		}
		int resp_code = getResponseStatuCode(req.getPath());
		resp.setStatuCode(resp_code);
		RequestProcessor reproc = null;
		if(resp_code == 200) {
			File fp = getFile(req.getPath());
			if(fp.isFile()) {
				reproc = new FileProcessor(fp);
				((FileProcessor)reproc).setBufferSize(this.bufferSize);
			} else if(fp.isDirectory()) {
				reproc = new DirProcessor(fp);
			}
		} else {
			reproc = new ErrorProcessor();
		}
		if(reproc != null){
			reproc.setService(getService());
			reproc.processRequest(req, resp);
		}
	}
}
