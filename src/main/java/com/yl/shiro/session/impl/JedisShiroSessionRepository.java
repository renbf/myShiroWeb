package com.yl.shiro.session.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.yl.shiro.session.SessionStatus;
import com.yl.shiro.session.ShiroSessionRepository;
/**
 * Session 管理
 * @author sojson.com
 *
 */
public class JedisShiroSessionRepository implements ShiroSessionRepository {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
    public static final String REDIS_SHIRO_SESSION = "redis-session:";
  //这里有个小BUG，因为Redis使用序列化后，Key反序列化回来发现前面有一段乱码，解决的办法是存储缓存不序列化
    public static final String REDIS_SHIRO_ALL = "*redis-session:*";
    private static final int SESSION_VAL_TIME_SPAN = 18000;
    private static final int DB_INDEX = 0;

    /**
	 * session status 
	 */
	public static final String SESSION_STATUS ="session-online-status";
	
    private RedisTemplate<String, Object> redisTemplate;
	private JdkSerializationRedisSerializer jdkSerializationRedisSerializer;
	private StringRedisSerializer stringRedisSerializer;

	
	
    public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
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

	@Override
    public void saveSession(Session session) {
        if (session == null || session.getId() == null)
            throw new NullPointerException("session is empty");
        try {
            final byte[] key = stringRedisSerializer.serialize(buildRedisSessionKey(session.getId()));
            
            
            //不存在才添加。
            if(null == session.getAttribute(SESSION_STATUS)){
            	//Session 踢出自存存储。
            	SessionStatus sessionStatus = new SessionStatus();
            	session.setAttribute(SESSION_STATUS, sessionStatus);
            }
            
            final byte[] value = jdkSerializationRedisSerializer.serialize(session);


            /**这里是我犯下的一个严重问题，但是也不会是致命，
             * 我计算了下，之前上面不小心给我加了0，也就是 18000 / 3600 = 5 个小时
             * 另外，session设置的是30分钟的话，并且加了一个(5 * 60)，一起算下来，session的失效时间是 5:35 的概念才会失效
             * 我原来是存储session的有效期会比session的有效期会长，而且最终session的有效期是在这里【SESSION_VAL_TIME_SPAN】设置的。
             *
             * 这里没有走【shiro-config.properties】配置文件，需要注意的是【spring-shiro.xml】 也是直接配置的值，没有走【shiro-config.properties】
             *
             * PS  : 注意： 这里我们配置 redis的TTL单位是秒，而【spring-shiro.xml】配置的是需要加3个0（毫秒）。
                long sessionTimeOut = session.getTimeout() / 1000;
                Long expireTime = sessionTimeOut + SESSION_VAL_TIME_SPAN + (5 * 60);
             */


            /*
            直接使用 (int) (session.getTimeout() / 1000) 的话，session失效和redis的TTL 同时生效
             */
            final int liveTime = (int) (session.getTimeout() / 1000);
            redisTemplate.execute(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(RedisConnection connection) throws DataAccessException {
                	connection.select(DB_INDEX);
                    connection.set(key, value);
                    if (liveTime > 0) {
                        connection.expire(key, liveTime);
                    }
                    return 1L;
                }
            });
        } catch (Exception e) {
        	logger.error(String.format("save session error，id:[%s]",session.getId()),e);
        }
    }

    @Override
    public void deleteSession(Serializable id) {
        if (id == null) {
            throw new NullPointerException("session id is empty");
        }
        try {
            final byte[] key = stringRedisSerializer.serialize(buildRedisSessionKey(id));
            redisTemplate.execute(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(RedisConnection connection) throws DataAccessException {
                	connection.select(DB_INDEX);
                    connection.del(key);
                    return 1L;
                }
            });
        } catch (Exception e) {
        	logger.error(String.format("删除session出现异常，id:[%s]",id),e);
        }
    }

   
	@Override
    public Session getSession(Serializable id) {
        if (id == null)
        	 throw new NullPointerException("session id is empty");
        Session session = null;
        try {
        	final byte[] key = stringRedisSerializer.serialize(buildRedisSessionKey(id));
        	session = (Session)redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                	connection.select(DB_INDEX);
                    byte[] value = connection.get(key);
                    return jdkSerializationRedisSerializer.deserialize(value);
                }
            });
        } catch (Exception e) {
        	logger.error(String.format("获取session异常，id:[%s]",id),e);
        }
        return session;
    }

    @Override
    public Collection<Session> getAllSessions() {
    	Collection<Session> sessions = null;
		try {
			final Set<String> keys = redisTemplate.keys(REDIS_SHIRO_ALL);
			sessions = (Collection<Session>)redisTemplate.execute(new RedisCallback<Collection<Session>>() {
                @Override
                public Collection<Session> doInRedis(RedisConnection connection) throws DataAccessException {
                	Set<Session> sessions = new HashSet<Session>();
                	connection.select(DB_INDEX);
                	for(String key:keys) {
                		byte[] keyb = stringRedisSerializer.serialize(key);
                		byte[] value = connection.get(keyb);
                		Session session = (Session)jdkSerializationRedisSerializer.deserialize(value);
                		sessions.add(session);
                	}
                    return sessions;
                }
            });
		} catch (Exception e) {
			logger.error("获取全部session异常",e);
		}
       
        return sessions;
    }

    private String buildRedisSessionKey(Serializable sessionId) {
        return REDIS_SHIRO_SESSION + sessionId;
    }
}
