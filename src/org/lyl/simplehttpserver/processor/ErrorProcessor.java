package org.lyl.simplehttpserver.processor;

import java.io.IOException;
import java.io.PrintWriter;

import org.lyl.simplehttpserver.core.Request;
import org.lyl.simplehttpserver.core.Response;

public class ErrorProcessor extends AbstractFileProcessor {
	
	public void processRequest(Request req, Response resp) throws IOException {
		_processError(req, resp);
	}
	
	/**
	 * Process some error
	 * @param code
	 * @param response_statu
	 * @throws IOException
	 */
	private void _processError(Request req, Response resp) throws IOException {
		PrintWriter writer = resp.getPrintWriter();
		int code = resp.getStatuCode();
		if(code == 403) {
			String html = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n<html><head>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=" + resp.getEncoding() + "\"><title>403 Forbidden</title>\n</head><body>\n<h1>Forbidden</h1>\n</body></html>\n";
			resp.setHeader("Content-Length", Integer.toString(html.getBytes(resp.getEncoding()).length));
			String header_str = resp.encodeHeader();
			writer.write(header_str + html);
		} else if(code == 400) {
			String html = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n<html><head>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=" + resp.getEncoding() + "\"><title>400 Bad Request</title>\n</head><body>\n<h1>Bad Request</h1>\n</body></html>\n";
			resp.setHeader("Content-Length", Integer.toString(html.getBytes(resp.getEncoding()).length));
			String header_str = resp.encodeHeader();
			writer.write(header_str + html);
		} else if(code == 404) {
			String html = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n<html><head>\n<meta http-equiv=\"content-type\" content=\"text/html; charset=" + resp.getEncoding() + "\"><title>404 Not Found</title>\n</head><body>\n<h1>Not Found</h1>\n</body></html>\n";
			resp.setHeader("Content-Length", Integer.toString(html.getBytes(resp.getEncoding()).length));
			String header_str = resp.encodeHeader();
			writer.write(header_str + html);
		}
	}
}
