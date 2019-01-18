package com.yl.shiro.cache;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import com.yl.redis.RedisUtil;
import com.yl.redis.SerializeUtils;
public class SpringRedisCache implements Cache{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private String name;
	private RedisUtil redisUtil;
	@Override
    public void clear() {
		logger.info("-------緩存清理------");
		redisUtil.flushAll();
    }

    @Override
    public void evict(Object key) {
    	logger.info("-------緩存刪除------");
    	byte[] keyb = SerializeUtils.serialize(key);
    	redisUtil.del(keyb);
    }

    @Override
    public ValueWrapper get(Object key) {
    	logger.info("------缓存获取-------"+key.toString());
    	byte[] keyb = SerializeUtils.serialize(key);
        byte[] valueb = redisUtil.get(keyb);
        Object object = SerializeUtils.unserialize(valueb);
        ValueWrapper obj = (object != null ? new SimpleValueWrapper(object) : null);
        logger.info("------获取到内容-------"+obj);
        return  obj;
    }

    @Override
    public void put(Object key, Object value) {
    	logger.info("-------加入缓存------");
    	logger.info("key----:"+key);
    	logger.info("key----:"+value);
    	byte[] keyb = SerializeUtils.serialize(key);
    	byte[] valueb = SerializeUtils.serialize(value);
    	redisUtil.set(keyb, valueb);
    }
    
    @Override
    public <T> T get(Object arg0, Class<T> arg1) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return redisUtil;
    }
    
    @Override
    public ValueWrapper putIfAbsent(Object arg0, Object arg1) {
        return null;
    }


    public void setName(String name) {
        this.name = name;
    }

	public RedisUtil getRedisUtil() {
		return redisUtil;
	}

	public void setRedisUtil(RedisUtil redisUtil) {
		this.redisUtil = redisUtil;
	}

	@Override
	public <T> T get(Object arg0, Callable<T> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
