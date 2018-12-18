package com.yl.shiro.session;

import java.io.Serializable;
import java.util.Collection;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CustomShiroSessionDAO extends AbstractSessionDAO{

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private ShiroSessionRepository shiroSessionRepository;
	
	public ShiroSessionRepository getShiroSessionRepository() {  
        return shiroSessionRepository;  
    }  
  
    public void setShiroSessionRepository(
            ShiroSessionRepository shiroSessionRepository) {  
        this.shiroSessionRepository = shiroSessionRepository;  
    }
    @Override  
    public void update(Session session) throws UnknownSessionException {  
    	shiroSessionRepository.saveSession(session);  
    }  
  
    @Override  
    public void delete(Session session) {  
        if (session == null) {  
        	logger.error(getClass()+ "Session 不能为null");
            return;  
        }  
        Serializable id = session.getId();  
        if (id != null)  
        	shiroSessionRepository.deleteSession(id);  
    }  
  
    @Override  
    public Collection<Session> getActiveSessions() {  
        return shiroSessionRepository.getAllSessions();  
    }  
  
    @Override  
    protected Serializable doCreate(Session session) {  
        Serializable sessionId = this.generateSessionId(session);  
        this.assignSessionId(session, sessionId);  
        shiroSessionRepository.saveSession(session);  
        return sessionId;  
    }  
  
    @Override  
    protected Session doReadSession(Serializable sessionId) {  
        return shiroSessionRepository.getSession(sessionId);  
    }

}
