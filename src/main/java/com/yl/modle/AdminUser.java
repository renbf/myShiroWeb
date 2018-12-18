package com.yl.modle;

import java.io.Serializable;
import java.util.Date;

/**
 * @author rbf
 * @createdate 2018/12/13 03:15
 * @desriction
 */
public class AdminUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2340557932235738712L;
	/**
	 * 用户id
	 */
	private Integer adminId;
	/**
	 * 用户登录用户名
	 */
	private String adminUserName;
	/**
	 * 用户名称
	 */
	private String adminName;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 角色id
	 */
	private Integer roleId;
	/**
	 * 创建时间
	 */
	private Date addTime;

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	public String getAdminUserName() {
		return adminUserName;
	}

	public void setAdminUserName(String adminUserName) {
		this.adminUserName = adminUserName;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	
}
