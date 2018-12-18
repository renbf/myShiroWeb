package com.yl.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yl.modle.AdminUser;
import com.yl.service.IAdminUserService;
import com.yl.shiro.realm.SampleRealm;
import com.yl.utils.Md5Utils;
/**
 * @author rbf
 * @createdate 2018/12/13 03:15
 * @desriction
 */
@Controller
@RequestMapping(value = "/adminUser")
public class AdminUserController {
	@Autowired
	private IAdminUserService adminUserService;
	@Autowired
	private SampleRealm sampleRealm;
	
	@RequestMapping(value = "/loginIndex")
	public ModelAndView loginIndex() {
		return new ModelAndView("login");
	}
	
	@RequestMapping(value = "/index")
	public ModelAndView index() {
		return new ModelAndView("index");
	}
	@RequestMapping(value = "/403")
	public ModelAndView noPermission() {
		return new ModelAndView("403");
	}
	
	@RequestMapping(value = "/quit")
	public ModelAndView quit() {
		
		Subject subject = SecurityUtils.getSubject();
		sampleRealm.getAuthorizationCache().clear();
		subject.logout();
		return new ModelAndView("login");
	}
	
	@RequestMapping(value = "/login",method = RequestMethod.POST, produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String login(String userName,String password,Boolean rememberMe) {
		password = Md5Utils.getMD5(password);
		if(rememberMe == null) {
			rememberMe = false;
		}
		UsernamePasswordToken token = new UsernamePasswordToken(userName,password,rememberMe);
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(token);
			return "成功";
		} catch (UnknownAccountException e) {
			return e.getMessage();
		} catch (DisabledAccountException e) {
			return e.getMessage();
		}
	}
	
	
	@RequestMapping(value = "/getAdminUserList")
	public ModelAndView getAdminUserList(HttpServletRequest request) {
		boolean permitted = SecurityUtils.getSubject().isPermitted("/getAdminUserList");
		if(!permitted) {
			return new ModelAndView("403");
		}
		List<AdminUser> list = adminUserService.queryAdminUserAll();
		ModelAndView mv = new ModelAndView("adminUser");
		Map<String, Object> result = new HashMap<>();
		result.put("list", list);
		result.put("name", "1fds");
		mv.addAllObjects(result);
		return mv;
	}
}
