package me.lb.controller.admin.common;

import javax.servlet.http.HttpSession;

import me.lb.model.system.User;
import me.lb.utils.MD5Util;
import me.lb.utils.UserUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/admin/common")
public class MainController {
	
	@ResponseBody
	@RequestMapping(value = "/init/{type}")
	public void init(@PathVariable String type) {
		// 初始化数据
		// FIXME
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@ResponseBody
	@RequestMapping(value = "/login")
	public String login(String loginName, String loginPass, HttpSession session) {
		try {
			// 后台验证，避免恶意操作
			if (StringUtils.isEmpty(loginName.trim())) {
				return "{ \"success\" : false, \"msg\" : \"用户名不能为空！\" }";
			}
			if (StringUtils.isEmpty(loginPass.trim())) {
				return "{ \"success\" : false, \"msg\" : \"密码不能为空！\" }";
			}
			// 通过验证，设置Shiro登录信息
			UsernamePasswordToken token = new UsernamePasswordToken();
			token.setUsername(loginName.trim());
			// 使用自定义的MD5进行密码处理
			String md5Pass = MD5Util.getValue(MD5Util.PREFIX + loginPass.trim());
			token.setPassword(md5Pass.toCharArray());
			// token.setRememberMe(true);
			// 通过Shiro进行登录（使用Realm的doGetAuthenticationInfo方法）
			SecurityUtils.getSubject().login(token);
			// 如果失败会抛出异常
			// 如果成功，则记录用户的信息
			// FIXME
			// User user = userService.findByLoginName(loginName.trim());
			// UserUtil.saveUserToSession(user, session);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"success\" : false, \"msg\" : \"用户名或密码错误！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/logout")
	public String logout() {
		try {
			SecurityUtils.getSubject().logout();
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"success\" : false }";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/change")
	public String change(String oldPass, String newPass, String rePass, HttpSession session) {
		try {
			// 读取登录用户信息
			User user = UserUtil.getUserFromSession(session);
			if (user != null) {
				if (user.getLoginPass().equals(MD5Util.getValue(MD5Util.PREFIX + oldPass.trim()))) {
					// 旧密码正确才能继续操作
					if (newPass.trim().equals(rePass.trim())) {
						// 两次输入的密码一致
						// FIXME
						// user.setLoginPass(MD5Util.getValue(MD5Util.PREFIX + newPass.trim()));
						// userService.update(user);
						return "{ \"success\" : true }";
					} else {
						return "{ \"msg\" : \"两次输入的密码不一致！\" }";
					}
				} else {
					return "{ \"msg\" : \"旧密码输入错误！\" }";
				}
			} else {
				return "{ \"msg\" : \"您没有登录！\" }";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"修改失败！\" }";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/forceChange")
	public String forceChange(String newPass, HttpSession session) {
		try {
			// 预留一个强制修改的路径，避免出现问题
			User user = UserUtil.getUserFromSession(session);
			if (user != null) {
				// FIXME
				// user.setLoginPass(MD5Util.getValue(MD5Util.PREFIX + newPass.trim()));
				// userService.update(user);
				return "{ \"success\" : true }";
			} else {
				return "{ \"msg\" : \"您没有登录！\" }";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"修改失败！\" }";
		}
	}

}