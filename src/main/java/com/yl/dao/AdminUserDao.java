
package com.yl.dao;
import com.yl.modle.AdminUser;
import java.util.List;

import org.apache.ibatis.annotations.Param;
/**
 * @author rbf
 * @createdate 2018/12/13 03:15
 * @desriction
 */
public interface AdminUserDao {


	public List<AdminUser> queryAdminUserAll();

	public AdminUser queryAdminUserByUserName(@Param("userName") String userName);
}
