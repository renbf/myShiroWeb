package com.yl.shiro.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
public class SpringRedisCache implements Cache{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private String name;
	private RedisTemplate<String, Object> redisTemplate;
	private JdkSerializationRedisSerializer jdkSerializationRedisSerializer;
	private StringRedisSerializer stringRedisSerializer;
	@Override
    public void clear() {
		logger.info("-------緩存清理------");
        redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "ok";
            }
        });
    }

    @Override
    public void evict(Object key) {
    	logger.info("-------緩存刪除------");
        final byte[] keyb=stringRedisSerializer.serialize(key.toString());
        redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.del(keyb);
            }
            
        });

    }

    @Override
    public ValueWrapper get(Object key) {
    	logger.info("------缓存获取-------"+key.toString());
    	final byte[] keyb=stringRedisSerializer.serialize(key.toString());
        Object object = null;
        object = redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] value = connection.get(keyb);
                if (value == null) {
                	logger.info("------缓存不存在-------");
                    return null;
                }
                return jdkSerializationRedisSerializer.deserialize(value);
            }
        });
        ValueWrapper obj=(object != null ? new SimpleValueWrapper(object) : null);
        logger.info("------获取到内容-------"+obj);
        return  obj;
    }

    @Override
    public void put(Object key, Object value) {
    	logger.info("-------加入缓存------");
    	logger.info("key----:"+key);
    	logger.info("key----:"+value);
    	final byte[] keyb=stringRedisSerializer.serialize(key.toString());
        final Object valuef = value;
        final long liveTime = 86400;
        redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] valueb = jdkSerializationRedisSerializer.serialize(valuef);
                connection.set(keyb, valueb);
                if (liveTime > 0) {
                    connection.expire(keyb, liveTime);
                }
                return 1L;
            }
        });

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
        return this.redisTemplate;
    }
    
    @Override
    public ValueWrapper putIfAbsent(Object arg0, Object arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setName(String name) {
        this.name = name;
    }

	public JdkSerializationRedisSerializer getJdkSerializationRedisSerializer() {
		return jdkSerializationRedisSerializer;
	}

	public void setJdkSerializationRedisSerializer(JdkSerializationRedisSerializer jdkSerializationRedisSerializer) {
		this.jdkSerializationRedisSerializer = jdkSerializationRedisSerializer;
	}

	public StringRedisSerializer getStringRedisSerializer() {
		return stringRedisSerializer;
	}

	public void setStringRedisSerializer(StringRedisSerializer stringRedisSerializer) {
		this.stringRedisSerializer = stringRedisSerializer;
	}
	
}
