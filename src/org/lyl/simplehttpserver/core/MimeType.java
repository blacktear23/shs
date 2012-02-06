package org.lyl.simplehttpserver.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MimeType {
	private static Logger log = LogManager.getLogger(MimeType.class);
	private static MimeType instance = null;
	private Map<String, String> type = new HashMap<String, String>();
	
	/**
	 * Load mime type
	 */
	private void init() {
		InputStream fis = null;
		//load default mime type
		try {
			fis = this.getClass().getResourceAsStream("mime.properties");
			if(fis != null) {
				loadMimeTypeFromInputStream(fis);
				log.info("Load system mime type.");
			} else {
				log.error("Cannot load system mime type.");
			}
		} catch(IOException ex) {
			log.error(ex);
		} finally {
			try{if(fis != null) fis.close();} catch(IOException ex) {}
		}
		
		//load the external mime type
		try {
			fis = new FileInputStream("mime.properties");
			loadMimeTypeFromInputStream(fis);
			log.info("Load additional mime type.");
		} catch(IOException ex) {
			//do nothing
		} finally {
			try{fis.close();} catch(IOException ex) {}
		}
	}
	
	private void loadMimeTypeFromInputStream(InputStream is) throws IOException {
		Properties prop = new Properties();
		prop.load(is);
		for(Object key : prop.keySet()) {
			type.put((String)key, prop.getProperty((String)key));
		}
	}
	
	private MimeType() {
		init();
	}
	
	public static MimeType getMimeType() {
		if(instance == null) {
			instance = new MimeType();
		}
		return instance;
	}
	
	public String resloveType(String fext) {
		String res = type.get(fext.toLowerCase());
		if(res == null) res = "text/bin";
		return res;
	}
}
