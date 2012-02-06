package org.lyl.simplehttpserver.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.lyl.simplehttpserver.Config;

public final class CacheEngine implements Cache {
	private static Logger log = LogManager.getLogger(CacheEngine.class);
	private Map<Object, Value> cache = new ConcurrentHashMap<Object, Value>();
	private AtomicInteger numObject = new AtomicInteger(0);
	private static CacheEngine instance = null;
	private Daemon daemon;
	private Object lock = new Object();
	private static final int HIT_TIME;
	private static final int MAX_NUM_CACHE;
	private static final int _HitMax;
	
	static {
		Config conf = Config.getConfig();
		HIT_TIME = conf.getCacheItemExpires();
		MAX_NUM_CACHE = conf.getNumCacheItem();
		_HitMax = Integer.MAX_VALUE - (int)(conf.getThreadPoolSize() * 1.1);
	}
	
	private CacheEngine(){
		this.daemon = new Daemon();
		Thread daemon = new Thread(this.daemon);
		daemon.setDaemon(true);
		daemon.start();
	}
	
	public void cache(Object key, Object obj) {
		numObject.incrementAndGet();
		if(numObject.get() > MAX_NUM_CACHE) {
			log.debug("Cache Over Flow!");
		}
		while(numObject.get() > MAX_NUM_CACHE) {
			forceCleanOld(0.5f);
			log.debug("Balance Once NUM:" + numObject.get());
		}
		cache.put(key, new Value(obj, HIT_TIME));		
	}
	
	public Object retive(Object key) {
		Value val = cache.get(key);
		if(val != null) {
			if(val.hit.get() < Integer.MAX_VALUE - _HitMax)
				val.hit.incrementAndGet();
			return val.value;
		}
		return null;
	}
	
	public static Cache getCache(){
		if(instance == null) {
			instance = new CacheEngine();
		}
		return instance;
	}
	
	private void cleanOld() {
		if(numObject.get() == 0) return;
		synchronized(lock) {
			for(Object key : this.cache.keySet()) {
				Value val = this.cache.get(key);
				if(val.hit.decrementAndGet() < 0) {
					this.cache.remove(key);
					this.numObject.decrementAndGet();
				}
			}
		}
	}
	
	private void forceCleanOld(float percent) {
		daemon.wait = true;
		int total = 0;
		synchronized(lock) {
			for(Value val : this.cache.values()) {
				total += val.hit.get();
			}
			total = (int)((total / (double)numObject.get()) * percent);
			for(Value val : this.cache.values()) {
				val.hit.addAndGet(-total);
			}
			cleanOld();
		}
		daemon.wait = false;
	}
	
	private static class Value {
		Object value;
		AtomicInteger hit;
		Value(Object value, int hit) {
			this.value = value;
			this.hit = new AtomicInteger(hit);
		}
	}
	
	private class Daemon implements Runnable {
		private boolean wait = false;
		
		public void run() {
			while(true) {
				try{
					TimeUnit.SECONDS.sleep(1);
				} catch(InterruptedException e) {
					log.info(e.toString());
				}
				if(wait) continue;
				
				CacheEngine.this.cleanOld();
			}
		}
	}
}
