package com.yl.shiro.realm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.yl.modle.AdminUser;
import com.yl.modle.Permission;
import com.yl.modle.Role;
import com.yl.service.IAdminUserService;
import com.yl.service.IPermissionService;
import com.yl.service.IRoleService;


/**
 * 
 * 开发公司：SOJSON在线工具 <p>
 * 版权所有：© www.sojson.com<p>
 * 博客地址：http://www.sojson.com/blog/  <p>
 * <p>
 * 
 * shiro 认证 + 授权   重写
 * 
 * <p>
 * 
 * 区分　责任人　日期　　　　说明<br/>
 * 创建　周柏成　2016年6月2日 　<br/>
 *
 * @author zhou-baicheng
 * @email  so@sojson.com
 * @version 1.0,2016年6月2日 <br/>
 * 
 */
public class SampleRealm extends AuthorizingRealm {

	@Autowired
	private IAdminUserService adminUserService;
	
	@Autowired
	private IPermissionService permissionService;
	
	@Autowired
	private IRoleService roleService;
	
	public SampleRealm() {
		super();
	}
	/**
	 *  认证信息，主要针对用户登录， 
	 */
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		AdminUser adminUser = adminUserService.queryAdminUserByUserName(token.getUsername());
		if(null == adminUser){
			throw new UnknownAccountException("帐号或密码不正确！");
		}else if(adminUser.getPassword().equals(token.getPassword())){
			throw new UnknownAccountException("帐号或密码不正确！");
		}else if(adminUser.getStatus() != 1){
			throw new DisabledAccountException("帐号已经禁止登录！");
		}
		return new SimpleAuthenticationInfo(adminUser,token.getPassword(), getName());
    }

	 /** 
     * 授权 
     */  
    @Override  
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {  
    	if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}
    	AdminUser adminUser = (AdminUser)SecurityUtils.getSubject().getPrincipal();
    	System.out.println(adminUser.getAdminName());
		SimpleAuthorizationInfo info =  new SimpleAuthorizationInfo();
		//根据用户ID查询角色（role），放入到Authorization里。
		List<Role> roleList = roleService.queryRoleAll();
		Set<String> roles = new HashSet<>();
		for(Role role:roleList) {
			roles.add(role.getRoleId().toString());
		}
		info.setRoles(roles);
		//根据用户ID查询权限（permission），放入到Authorization里。
		List<Permission> permissionList = permissionService.queryPermissionAll();
		Set<String> permissions = new HashSet<>();
		for(Permission p:permissionList) {
			permissions.add(p.getPermissionUrl());
		}
		info.setStringPermissions(permissions);
        return info;  
    }  
    /**
     * 清空当前用户权限信息
     */
	public  void clearCachedAuthorizationInfo() {
		PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
		SimplePrincipalCollection principals = new SimplePrincipalCollection(
				principalCollection, getName());
		super.clearCachedAuthorizationInfo(principals);
	}
	/**
	 * 指定principalCollection 清除
	 */
	public void clearCachedAuthorizationInfo(PrincipalCollection principalCollection) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(
				principalCollection, getName());
		super.clearCachedAuthorizationInfo(principals);
	}
	
	
}
