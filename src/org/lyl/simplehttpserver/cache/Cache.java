package org.lyl.simplehttpserver.cache;

public interface Cache {
	void cache(Object key, Object obj);
	Object retive(Object key);
}