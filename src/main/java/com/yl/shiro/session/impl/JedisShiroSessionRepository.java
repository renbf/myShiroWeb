package com.yl.shiro.session.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.yl.redis.SerializeUtils;
import com.yl.shiro.session.SessionStatus;
import com.yl.shiro.session.ShiroSessionRepository;

import redis.clients.jedis.JedisCluster;
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
	@Autowired
	private JedisCluster jedisCluster;
	

	@Override
    public void saveSession(Session session) {
        if (session == null || session.getId() == null)
            throw new NullPointerException("session is empty");
        try {
        	String sessionid = buildRedisSessionKey(session.getId());
            byte[] key = SerializeUtils.serializeStr(sessionid,SerializeUtils.UTF_8);
            
            //不存在才添加。
            if(null == session.getAttribute(SESSION_STATUS)){
            	//Session 踢出自存存储。
            	SessionStatus sessionStatus = new SessionStatus();
            	session.setAttribute(SESSION_STATUS, sessionStatus);
            }
            
            byte[] value = SerializeUtils.serialize(session);


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
            int liveTime = (int) (session.getTimeout() / 1000);
            jedisCluster.setex(key, liveTime, value);
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
        	String sessionid = buildRedisSessionKey(id);
            byte[] key = SerializeUtils.serializeStr(sessionid,SerializeUtils.UTF_8);
            jedisCluster.del(key);
        } catch (Exception e) {
        	logger.error(String.format("删除session出现异常，id:[%s]",id),e);
        }
    }

   
	@Override
    public Session getSession(Serializable id) {
        if (id == null){
        	throw new NullPointerException("session id is empty");
        }
        Session session = null;
        try {
        	String sessionid = buildRedisSessionKey(id);
        	byte[] key = SerializeUtils.serializeStr(sessionid,SerializeUtils.UTF_8);
        	byte[] value = jedisCluster.get(key);
        	session = (Session)SerializeUtils.unserialize(value);
        } catch (Exception e) {
        	logger.error(String.format("获取session异常，id:[%s]",id),e);
        }
        return session;
    }

    @Override
    public Collection<Session> getAllSessions() {
    	Collection<Session> sessions = null;
		try {
			Set<String> keys = jedisCluster.hkeys(REDIS_SHIRO_ALL);
			for(String key:keys) {
				byte[] keyb = SerializeUtils.serializeStr(key, SerializeUtils.UTF_8);
				byte[] valueb = jedisCluster.get(keyb);
				Session session = (Session)SerializeUtils.unserialize(valueb);
				sessions.add(session);
        	}
		} catch (Exception e) {
			logger.error("获取全部session异常",e);
		}
        return sessions;
    }

    private String buildRedisSessionKey(Serializable sessionId) {
        return REDIS_SHIRO_SESSION + sessionId;
    }
    
}
