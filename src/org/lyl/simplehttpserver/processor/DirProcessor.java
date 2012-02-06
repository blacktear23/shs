package org.lyl.simplehttpserver.processor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.*;

import org.lyl.simplehttpserver.Config;
import org.lyl.simplehttpserver.core.Request;
import org.lyl.simplehttpserver.core.Response;

public class DirProcessor extends AbstractFileProcessor implements RequestProcessor {
	private File fp;
	
	public DirProcessor(File fp) {
		this.fp = fp;
	}
	
	public void processRequest(Request req, Response resp) throws IOException {
		_listDirFilesToCLient(fp, req, resp);
	}
	
	/**
	 * If user request a dir, list the files in this dir
	 * @param fp
	 * @param path
	 * @param response_statu
	 * @throws IOException
	 */
	private void _listDirFilesToCLient(File fp, Request req, Response resp) throws IOException {
		String path = req.getPath();
		String key = fp.getPath() + fp.lastModified();
		if((path.endsWith("/") || path.endsWith("\\") ) && !path.equals("/")) path = path.substring(0, path.length() - 1);
		
		String chtml = (String)getCache().retive(key);
		if(chtml == null) {
			StringBuilder html = new StringBuilder("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n");
			html.append("<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=" + resp.getEncoding() + "\"><title>Folder:" + path + "</title></head>\n");
			html.append("<body><h2>File in " + path + "</h2>\n<hr /><ul>");
			if(!path.equals("/") && !path.equals("\\"))
				html.append("<li><a href=\"../\">..</a></li>");
			File[] fps = fp.listFiles();
			sortFileArray(fps);
			for(File efp : fps) {
				if(efp.isDirectory())
					html.append("<li><table><tr><td width=\"400\"><a href=\"./" + efp.getName() + "/\">" + efp.getName() + "/</a></td><td>" + new Date(efp.lastModified()).toString() + "</td></tr></table></li>\n");
				else
					html.append("<li><table><tr><td width=\"400\"><a href=\"./" + efp.getName() + "\">" + efp.getName() + "</a></td><td>" + new Date(efp.lastModified()).toString() + "</td><td>&nbsp;&nbsp;&nbsp;&nbsp;"  + efp.length() + " Byte</td></tr></table></li>\n");
			}
			html.append("</ul><hr /><p>" + getService().getServerName() + " " + Config.VERSION + "</p></body></html>");
			chtml = html.toString();
			getCache().cache(key, chtml);
		}
		//send it back
		resp.setHeader("Content-Length", Integer.toString(chtml.getBytes(resp.getEncoding()).length));
		resp.setHeader("Content-Type", "text/html; charset=" + resp.getEncoding());
		String header_str = resp.encodeHeader();
		resp.getPrintWriter().write(header_str + chtml);
	}
	
	private void sortFileArray(File[] array) {
		Arrays.sort(array, new Comparator<File>(){
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
	}
}
