package com.yl.service;

import java.util.List;
import com.yl.modle.AdminUser;
/**
 * @author rbf
 * @createdate 2018/12/13 03:15
 * @desriction
 */
public interface IAdminUserService {
public List<AdminUser> queryAdminUserAll();
public AdminUser queryAdminUserByUserName(String userName);
}
