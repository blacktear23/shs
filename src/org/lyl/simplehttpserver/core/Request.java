package org.lyl.simplehttpserver.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Request {
	private static Logger log = LogManager.getLogger(Request.class);
	private String type = "";
	private String path = "";
	private String http_version = "";
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String> paramter = new HashMap<String, String>();
	
	private static final int BUFFER_SIZE = 1024;
	private Socket client;
	
	public Request(Socket client) throws IOException {
		this.client = client;
		init();
	}
	
	/**
	 * get information from user request
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private void init() throws IOException {
		InputStream is = new BufferedInputStream(client.getInputStream());
		StringBuilder header = new StringBuilder();
		byte[] buf = new byte[BUFFER_SIZE];
		int num;
		while((num = is.read(buf)) > 0) {
			header.append(new String(buf, 0, num));
			if(num < BUFFER_SIZE) break;
		}
		
		String[] each_line = header.toString().split("\n");			
		String first_line = each_line[0];
		String[] fline_res = processFirstLine(first_line);
		this.type = fline_res[0];
		String[] path_param = convertFromWebEcode(fline_res[1]).split("\\?", 2);
		this.path = path_param[0];
		if(path_param.length == 2)
			handleParam(path_param[1]);
		this.http_version = fline_res[2];
		if(this.type.equals("POST")) {
			int datapos = header.indexOf("\r\n\r\n") + 4;
			String postdata = header.substring(datapos);
			handleParam(postdata);
		}
		
		for(int i = 1; i < each_line.length; i++) {
			String[] kvp = each_line[i].split(":", 2);
			if(kvp.length == 2)
				headers.put(kvp[0].trim(), kvp[1].trim());
		}
	}
	
	/**
	 * param: a.html?a=10&b=20
	 * @param param
	 */
	private void handleParam(String param) {
		String[] params = param.split("[&]");
		int pos;
		for(String val : params) {
			pos = val.lastIndexOf("=");
			if(pos == -1 && params.length == 1) {
				this.paramter.put("DEFAULT", val);
			}else{
				this.paramter.put(val.substring(0, pos), val.substring(pos+1));
			}
		}
	}
	
	/**
	 * get the request first line's information
	 * @param fline
	 * @return
	 */
	private String[] processFirstLine(String fline) {
		String[] res = new String[3];
		int start_http = fline.lastIndexOf("HTTP");
		if(fline.startsWith("GET ")) {
			res[0] = "GET";
			res[1] = fline.substring(4, start_http - 1);
		} else if(fline.startsWith("POST ")) {
			res[0] = "POST";
			res[1] = fline.substring(5, start_http - 1);
		} else if(fline.startsWith("PUT ")) {
			res[0] = "PUT";
			res[1] = fline.substring(4, start_http - 1);
		} else if(fline.startsWith("DELETE ")) {
			res[0] = "DELETE";
			res[1] = fline.substring(7, start_http - 1);
		} else {
			res[0] = "";
			res[1] = "";
			res[2] = "";
			return res;
		}
		res[2] = fline.substring(start_http + 5, fline.length() - 1);
		return res;
	}
	
	private String convertFromWebEcode(String src) {
		try {
			return URLDecoder.decode(src, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString());
		}
		return src;
	}

	public String getType() {
		return type;
	}

	public String getPath() {
		return path;
	}

	public String getHttpVersion() {
		return http_version;
	}

	public Socket getSocket() {
		return client;
	}
	
	public boolean containsHeader(String key) {
		return headers.containsKey(key);
	}
	
	public String getHeader(String key) {
		return headers.get(key);
	}
	
	public String getParamter(String key) {
		return paramter.get(key);
	}
}
