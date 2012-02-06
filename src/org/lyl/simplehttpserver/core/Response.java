package org.lyl.simplehttpserver.core;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Response {
	private Map<String, String> header = new HashMap<String, String>();
	private String encoding = "UTF-8";
	private int statu_code = 0;
	
	private Socket client;
	private OutputStream sos;
	private PrintWriter writer;
	private String http_version = "1.x";
	
	public Response(Socket client) throws IOException {
		this.client = client;
		sos = new BufferedOutputStream(client.getOutputStream());
		try {
			writer = new PrintWriter(
					new OutputStreamWriter(sos, getEncoding()));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public OutputStream getOutputStream() {
		return sos;
	}
	
	public Socket getSocket() {
		return client;
	}
	
	public void setHttpVersion(String version) {
		this.http_version = version;
	}
	
	public String getHttpVersion() {
		return this.http_version;
	}
	
	public PrintWriter getPrintWriter() {
		return writer;
	}
	
	public void setHeader(String key, String value) {
		header.put(key, value);
	}
	
	public String getHeader(String key) {
		return header.get(key);
	}
	
	public void setStatuCode(int code) {
		this.statu_code = code;
	}
	
	public int getStatuCode() {
		return this.statu_code;
	}
	
	public String encodeHeader() {
		return getResponseLine(statu_code) + getHeaderString();
	}
	
	public void setMimeType(String type) {
		header.put("Content-Type", type);
	}
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Generate the first response line
	 * @param code
	 * @return
	 */
	private String getResponseLine(int code) {
		String res = null;
		switch(code){
		case 200:
			res = "HTTP/" + http_version + " 200 OK\r\n";
			break;
		case 404:
			res = "HTTP/" + http_version + " 404 Not Found\r\n";
			break;
		case 403:
			res = "HTTP/" + http_version + " 403 Forbidden\r\n";
			break;
		case 206:
			res = "HTTP/" + http_version + " 206 Partial Content\r\n";
			break;
		case 304:
			res = "HTTP/" + http_version + " 304 Not Modified\r\n";
			break;
		case 400:
			res = "HTTP/" + http_version + " 400 Bad Request\r\n";
			break;
		}
		return res;
	}
	
	/**
	 * Generate header string
	 * @return
	 */
	private String getHeaderString() {
		StringBuilder sb = new StringBuilder();
		for(String key : header.keySet()) {
			sb.append(key + ": " + header.get(key) + "\r\n");
		}
		sb.append("\r\n");
		return sb.toString();
	}
	
	public void flush() throws IOException {
		writer.flush();
		sos.flush();
	}
}
