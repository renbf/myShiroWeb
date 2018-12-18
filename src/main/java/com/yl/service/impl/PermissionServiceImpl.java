package com.yl.service.impl;


import com.yl.dao.PermissionDao;
import com.yl.modle.Permission;
import com.yl.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 * @author rbf
 * @createdate 2018/12/13 03:15
 * @desriction
 */
@Service
public class PermissionServiceImpl implements IPermissionService{
@Autowired
private PermissionDao permissionDao;

public List<Permission> queryPermissionAll(){

        return permissionDao.queryPermissionAll();
        }
}
