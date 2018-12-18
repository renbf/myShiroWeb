package com.yl.modle;

import java.io.Serializable;
/**
 * @author rbf
 * @createdate 2018/12/13 03:15
 * @desriction
 */
public class Permission implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = -5895269332967723828L;
/**
*权限id
*/
private Integer permissionId;
/**
*权限url
*/
private String permissionUrl;
/**
*权限描述
*/
private String permissionDeac;
public Integer getPermissionId() {
	return permissionId;
}
public void setPermissionId(Integer permissionId) {
	this.permissionId = permissionId;
}
public String getPermissionUrl() {
	return permissionUrl;
}
public void setPermissionUrl(String permissionUrl) {
	this.permissionUrl = permissionUrl;
}
public String getPermissionDeac() {
	return permissionDeac;
}
public void setPermissionDeac(String permissionDeac) {
	this.permissionDeac = permissionDeac;
}

}
