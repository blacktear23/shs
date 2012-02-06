package org.lyl.simplehttpserver.processor;

import java.io.File;
import java.io.IOException;

public abstract class AbstractFileProcessor extends AbstractRequestProcessor {
	private String documentPath;

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}

	/**
	 * generate response code
	 * @param path
	 * @return
	 */
	public int getResponseStatuCode(String path) {
		if(path.equals("")) 
			return 400;
		File fp = getFile(path);
		if(fp.exists()) {
			if(hasPermit(fp))
				return 200;
			else
				return 403;
		} else {
			return 404;
		}
	}
	
	public File getFile(String path) {
		File fp = new File(getDocumentPath() + path);
		return fp;
	}
	
	/**
	 * check if use can access this file
	 * @param fp
	 * @return
	 */
	public boolean hasPermit(File fp) {
		return fp.canRead() && !isOutOfDocumentRoot(fp); 
	}
	
	public boolean isOutOfDocumentRoot(File fp) {
		try{
			String path = fp.getCanonicalPath();
			return documentPath.length() > path.length();
		}catch(IOException ex) {
			return false;
		}
	}
}
