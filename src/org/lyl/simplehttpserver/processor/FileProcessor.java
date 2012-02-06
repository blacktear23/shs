package org.lyl.simplehttpserver.processor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.lyl.simplehttpserver.cache.Cache;
import org.lyl.simplehttpserver.cache.CacheEngine;
import org.lyl.simplehttpserver.core.Request;
import org.lyl.simplehttpserver.core.Response;

public class FileProcessor extends AbstractFileProcessor {
	private Cache cache = CacheEngine.getCache();
	private File fp;
	private int bufferSize = 1024;
	
	
	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public FileProcessor(File fp) {
		this.fp = fp;
	}
	
	@SuppressWarnings("deprecation")
	private boolean processFileModified(Request req, File fp) {
		//process if modified
		if(req.containsHeader("If-None-Match")) {
			String etag = req.getHeader("If-None-Match");
			if(etag.equals(generateETag(fp))) {
				return true;
			} else {
				return false;
			}
		} 
		if(req.containsHeader("If-Modified-Since")) {
			try {
				long time = Date.parse(req.getHeader("If-Modified-Since"));
				if(fp.lastModified() <= time) {
					return true;
				}
			} catch(IllegalArgumentException ex) {
				//do nothing
			}
		}
		return false;
	}
	
	
	public void processRequest(Request req, Response resp) throws IOException {
		if(processFileModified(req, fp)) {
			_sendNotModified(req, resp);
			return;
		}
		
		if(req.containsHeader("Range")) {
			_sendExistFileWithRange(fp, req, resp);
		} else {
			_sendExistFileToClient(fp, req, resp);
		}
	}
	
	private void _sendNotModified(Request req, Response resp) throws IOException {
		resp.setStatuCode(304);
		resp.setHeader("Last-Modified", req.getHeader("If-Modified-Since"));
		resp.setHeader("Etag", generateETag(fp));
		OutputStream os = resp.getOutputStream();
		os.write(resp.encodeHeader().getBytes());
		os.flush();
	}
	
	/**
	 * send the file to client
	 * @param fp
	 * @param response_statu
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private void _sendExistFileToClient(File fp, Request req, Response resp) throws IOException {
		long content_length = fp.length();
		String etag = generateETag(fp);
		boolean cached = false;
		resp.setHeader("Content-Length", Long.toString(content_length));
		
		resp.setHeader("Last-Modified", new Date(fp.lastModified()).toGMTString());
		resp.setHeader("Etag", etag);
		
		String header_str = resp.encodeHeader();
		OutputStream os = resp.getOutputStream();
		//send header
		os.write(header_str.getBytes());
		InputStream fis = null;
		byte[] buf = null;
		if(content_length < this.getBufferSize()){
			Object ref = cache.retive(etag);
			if(ref != null){
				buf = (byte[])ref;
				cached = true;
			}else{
				buf = new byte[this.getBufferSize()];
			}
		}else{
			buf = new byte[this.getBufferSize()];
		}
		
		try {
			if(!cached) {
				fis = new BufferedInputStream(new FileInputStream(fp));
				int numr = 0;
				while((numr = fis.read(buf)) > 0) {
					if(content_length < this.getBufferSize())
						cache.cache(etag, buf);
					os.write(buf, 0, numr);
				}
			} else {
				os.write(buf, 0, (int) content_length);
			}
			os.flush();
		} catch(IOException ex) {
			throw ex;
		} finally {
			if(fis != null) fis.close();
		}
	}
	
	/**
	 * send the file to client special for Range header
	 * @param fp
	 * @param req
	 * @throws IOException
	 */
	private void _sendExistFileWithRange(File fp, Request req, Response resp) throws IOException {
		resp.setStatuCode(206);
		String range = req.getHeader("Range").substring(6);
		String[] rse = range.split("-", 2);
		long start = 0, end = 0, fstart = 0;
		boolean bstart = false, bend = false;
		long content_len = 0;
		
		try {
			if(!rse[0].equals("")) {
				start = Long.parseLong(rse[0]);
				bstart = true;
			}
			if(!rse[1].equals("")) {
				end = Long.parseLong(rse[1]);
				bend = true;
			}
		} catch(NumberFormatException ex) {
			//do nothing
		}
		
		if(bstart && !bend) {
			content_len = fp.length() - start;
			fstart = start;
		} else if(bstart && bend) {
			content_len = end - start;
			fstart = start;
		} else if(!bstart && bend) {
			content_len = fp.length() - end;
			fstart = 0;
		}
		
		resp.setHeader("Content-Length", Long.toString(fp.length()));
		resp.setHeader("Content-Range", "bytes " + fstart + "-" + (fp.length() - 1) + "/" + fp.length());
		String header_str = resp.encodeHeader();
		OutputStream os = resp.getOutputStream();
		//send header
		os.write(header_str.getBytes());
		InputStream fis = null;
		byte[] buf = new byte[this.getBufferSize()];
		try {
			fis = new BufferedInputStream(new FileInputStream(fp));
			int numr = 0, sum = 0;
			fis.skip(fstart);
			while((numr = fis.read(buf)) > 0) {
				sum += numr;
				if(sum > content_len)
					os.write(buf, 0, (int)(content_len - sum));
				else
					os.write(buf, 0, numr);
			}
			os.flush();
		} catch(IOException ex) {
			throw ex;
		} finally {
			if(fis != null) fis.close();
		}
	}
	
	private String generateETag(File fp) {
		String str = fp.getName() + fp.length() + fp.lastModified();
		return "\"" + str + "\"";
	}
}
