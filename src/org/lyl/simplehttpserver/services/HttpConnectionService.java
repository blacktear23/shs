package org.lyl.simplehttpserver.services;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.lyl.simplehttpserver.core.ConnectionService;
import org.lyl.simplehttpserver.core.MimeType;
import org.lyl.simplehttpserver.core.Request;
import org.lyl.simplehttpserver.core.RequestFilter;
import org.lyl.simplehttpserver.core.Response;
import org.lyl.simplehttpserver.processor.RequestProcessor;

public class HttpConnectionService implements ConnectionService {
	private static final int SECOND = 1000;
	private static Logger log = LogManager.getLogger(HttpConnectionService.class);
	private static AtomicInteger _count = new AtomicInteger(0);
	private String serverName = "Simple Http Server";
	private long expiresTime = 0;
	private boolean enableKeepAlive = true;
	private int maxKeepAlive = 50;
	private int keepAliveTimeout = 0;
	private RequestFilter filter;
	private String encoding = "UTF-8";
	private boolean running = true;

	/**
	 * Client's Socket
	 */
	private Socket client;
	
	public HttpConnectionService() {
		log.debug("New ConnectionService");
	}
	
	public void setClient(Socket client) {
		this.client = client;
	}
	
	/**
	 * Thread code here
	 */
	public void run() {
		try{
			_count.incrementAndGet();
			while(running){
				Request req = new Request(client);
				String path = req.getPath();
				if(!path.startsWith("/")){
					path = "BAD REQUEST";
				}
				Response resp = new Response(client);
				resp.setEncoding(getEncoding());
				resp.setHttpVersion(req.getHttpVersion());
				setSystemDefaultHeader(req, resp);
				RequestProcessor reqproc = filter.getRequestProcessor(req.getPath());
				reqproc.setService(this);
				log.info(req.getType() + " " + path + " From:" + client.getRemoteSocketAddress().toString());
				
				reqproc.processRequest(req, resp);
				resp.flush();
				if(resp.getHeader("Connection").toLowerCase().equals("close")){
					break;
				}
			}
		}catch(SocketTimeoutException ex) {
			log.info("Client " + client.getRemoteSocketAddress().toString() + " Timeout.");
		}catch(Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				_count.getAndDecrement();
				client.close();
			} catch (IOException e) {
				log.error(e);
			}
		}
	}
	
	/**
	 * Get MIME type
	 * @param req the Request Object 
	 * @return
	 */
	private String getMimeType(Request req) {
		String fext = req.getPath().substring(req.getPath().lastIndexOf('.') + 1);
		return MimeType.getMimeType().resloveType(fext);
	}
	
	/**
	 * Set some system headers
	 * @param req
	 */
	@SuppressWarnings("deprecation")
	public void setSystemDefaultHeader(Request req, Response resp) {
		resp.setStatuCode(200);
		Date now = new Date();
		resp.setHeader("Server", this.getServerName());
		resp.setHeader("Content-Type", getMimeType(req));
		resp.setHeader("Date", now.toGMTString());
		resp.setHeader("Expires", new Date(now.getTime() + this.getExpiresTime() * SECOND).toGMTString());
		//process keep alive
		if(this.isEnableKeepAlive() &&
				_count.get() <= this.getMaxKeepAlive() &&
				req.containsHeader("Connection") && 
				req.getHeader("Connection").toLowerCase().equals("keep-alive")){
			resp.setHeader("Connection", "Keep-Alive");
			int timeout = this.getKeepAliveTimeout();
			if(timeout == 0){
				if(req.containsHeader("Keep-Alive")) {
					timeout = Integer.parseInt(req.getHeader("Keep-Alive"));
				}
			}
			try {
				client.setSoTimeout(timeout * SECOND);
			} catch(SocketException ex) {
				log.error(ex);
			}
			resp.setHeader("Keep-Alive", "timeout=" + timeout + ", max=" + (this.getMaxKeepAlive() - _count.get()));
		} else {
			resp.setHeader("Connection", "Close");
		}
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public long getExpiresTime() {
		return expiresTime;
	}

	public void setExpiresTime(long expiresTime) {
		this.expiresTime = expiresTime;
	}
	
	public boolean isEnableKeepAlive() {
		return enableKeepAlive;
	}

	public void setEnableKeepAlive(boolean enableKeepAlive) {
		this.enableKeepAlive = enableKeepAlive;
	}

	public int getMaxKeepAlive() {
		return maxKeepAlive;
	}

	public void setMaxKeepAlive(int maxKeepAlive) {
		this.maxKeepAlive = maxKeepAlive;
	}

	public int getKeepAliveTimeout() {
		return keepAliveTimeout;
	}

	public void setKeepAliveTimeout(int keepAliveTimeout) {
		this.keepAliveTimeout = keepAliveTimeout;
	}
	
	public RequestFilter getFilter() {
		return filter;
	}

	public void setFilter(RequestFilter filter) {
		this.filter = filter;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public void shutdown() {
		running = false;
	}
}
