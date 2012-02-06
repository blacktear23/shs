package org.lyl.simplehttpserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Config {
	private static Logger log = LogManager.getLogger(Config.class);
	private static Config instance = null;
	public static final String VERSION = "v 0.5.3 beta"; 
	private int Thread_Pool_Size = 100;
	private String beanConfigFile = "src/config.xml";
	private int cacheItemExpires = 10;
	private int numCacheItem = 200;
	private boolean autoRestart = false;
	
	public String getBeanConfigFileName() {
		return beanConfigFile;
	}

	public int getThreadPoolSize() {
		return Thread_Pool_Size;
	}
	
	public int getCacheItemExpires() {
		return cacheItemExpires;
	}
	
	public int getNumCacheItem() {
		return numCacheItem;
	}
	
	public boolean getAutoRestart(){
		return autoRestart;
	}
	
	private synchronized void init() {
		Properties prop = new Properties();
		FileInputStream is = null;
		try {
			is = new FileInputStream("./config.properties");
			prop.load(is);

			if(prop.containsKey(KEY_POOL_SIZE))
				this.Thread_Pool_Size = Integer.parseInt(prop.getProperty(KEY_POOL_SIZE));
			if(prop.containsKey(KEY_BEAN_CONFIG))
				this.beanConfigFile = prop.getProperty(KEY_BEAN_CONFIG);
			if(prop.containsKey(KEY_CACHE_EXPIRE))
				this.cacheItemExpires = Integer.parseInt(prop.getProperty(KEY_CACHE_EXPIRE));
			if(prop.containsKey(KEY_CACHE_NUMITEM))
				this.numCacheItem = Integer.parseInt(prop.getProperty(KEY_CACHE_NUMITEM));
			if(prop.containsKey(KEY_AUTORESTART))
				this.autoRestart = Boolean.parseBoolean(prop.getProperty(KEY_AUTORESTART));
			
		} catch (Exception e) {
			log.info("Cannot load configuration, use default");
		} finally {
			if(is != null ) try {is.close(); } catch(IOException ex) {}
		}
	}
	
	private Config() {
		init();
	}
	
	public static Config getConfig() {
		if(instance == null) {
			instance = new Config();
		}
		return instance;
	}
	private static final String KEY_POOL_SIZE = "server.pool.size";
	private static final String KEY_BEAN_CONFIG = "server.beanconfig";
	private static final String KEY_CACHE_EXPIRE = "server.cache.expirestime";
	private static final String KEY_CACHE_NUMITEM = "server.cache.numitem";
	private static final String KEY_AUTORESTART = "server.autorestart";
}
