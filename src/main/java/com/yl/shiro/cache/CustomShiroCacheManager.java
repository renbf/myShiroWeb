package com.yl.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;

public class CustomShiroCacheManager implements CacheManager,Destroyable{
	
	private SpringRedisCache cacheManager;
	@Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		return new RedisCache<K, V>(name,cacheManager);
	}
	
	@Override
	public void destroy() throws Exception {
		cacheManager = null;
	}

	public SpringRedisCache getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(SpringRedisCache cacheManager) {
		this.cacheManager = cacheManager;
	}

}
