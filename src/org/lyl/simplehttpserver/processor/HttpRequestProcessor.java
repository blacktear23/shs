package org.lyl.simplehttpserver.processor;

import java.io.File;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.lyl.simplehttpserver.core.Request;
import org.lyl.simplehttpserver.core.Response;

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

	public void processRequest(Request req, Response resp) throws Exception {
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
