package com.yl.controller;

import com.yl.modle.Role;
import com.yl.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author rbf
 * @createdate 2018/12/13 03:15
 * @desriction
 */
@Controller
public class RoleController {
@Autowired
private IRoleService roleService;
@RequestMapping(value = "/getRoleList")
public ModelAndView getRoleList(HttpServletRequest request) {
        List<Role> list = roleService.queryRoleAll();
        ModelAndView mv = new ModelAndView("role");
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("name", "1fds");
        mv.addAllObjects(result);
        return mv;
        }
}
