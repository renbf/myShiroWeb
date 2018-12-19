package com.yl.shiro.cache;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache.ValueWrapper;
public class RedisCache <K, V> implements Cache<K, V>{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final String REDIS_SHIRO_CACHE = "shiro-cache:";
	private SpringRedisCache cacheManager;
	private String name;
	
	public RedisCache(String name,SpringRedisCache cacheManager) {
		this.cacheManager=cacheManager;
		this.name = name;
	}
	
	public SpringRedisCache getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(SpringRedisCache cacheManager) {
		this.cacheManager = cacheManager;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String getCacheKey(Object key) {
		return this.REDIS_SHIRO_CACHE + getName() + ":" + key.getClass().getName();
	}
	
	@Override
	public V get(K key) throws CacheException {
		if(key == null) {
			logger.info("key ========== null");
			return null;
		}
		String keyStr = getCacheKey(key);
		ValueWrapper valueWrapper = cacheManager.get(keyStr);
		if(valueWrapper == null) {
			logger.info("get(key):"+key+"is null");
			return null;
		}
		return (V)valueWrapper.get();
	}

	@Override
	public V put(K key, V value) throws CacheException {
		V v= get(key);
		String keyStr = getCacheKey(key);
		cacheManager.put(keyStr, value);
		if(v == null) {
			return value;
		}else {
			return v;
		}
	}

	@Override
	public V remove(K key) throws CacheException {
		V v = get(key);
		if(v == null) {
			logger.info("remove(key)========"+key+" is null");
			return null;
		}else {
			String keyStr = getCacheKey(key);
			cacheManager.evict(keyStr);
			return v;
		}
	}

	@Override
	public void clear() throws CacheException {
		cacheManager.clear();
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Set<K> keys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}

}
