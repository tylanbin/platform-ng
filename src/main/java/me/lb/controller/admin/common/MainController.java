package me.lb.controller.admin.common;

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpSession;

import me.lb.model.system.User;
import me.lb.service.system.UserService;
import me.lb.utils.MD5Util;
import me.lb.utils.UserUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/admin/common")
public class MainController {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private LocalSessionFactoryBean sessionFactory;

	@Autowired
	private UserService userService;

	@ResponseBody
	@RequestMapping(value = "/init/{type}")
	public void init(@PathVariable String type) {
		// 重建数据库结构
		Configuration cfg = sessionFactory.getConfiguration();
		SchemaExport se = new SchemaExport(cfg);
		se.create(false, true);
		// 初始化数据
		try {
			// 重建Activiti的表
			InputStream input = this.getClass().getClassLoader().getResourceAsStream("sql/activiti.sql");
			List<String> sqls = IOUtils.readLines(input, "utf-8");
			if (!sqls.isEmpty()) {
				jdbcTemplate.batchUpdate(sqls.toArray(new String[sqls.size()]));
			}
			if ("test".equals(type)) {
				// 使用测试数据初始化
				input = this.getClass().getClassLoader().getResourceAsStream("sql/test.sql");
				sqls = IOUtils.readLines(input, "utf-8");
				if (!sqls.isEmpty()) {
					jdbcTemplate.batchUpdate(sqls.toArray(new String[sqls.size()]));
				}
			} else if ("normal".equals(type)) {
				// 使用正式数据初始化
				input = this.getClass().getClassLoader().getResourceAsStream("sql/normal.sql");
				sqls = IOUtils.readLines(input, "utf-8");
				if (!sqls.isEmpty()) {
					jdbcTemplate.batchUpdate(sqls.toArray(new String[sqls.size()]));
				}
			}
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
			User user = userService.findByLoginName(loginName.trim());
			UserUtil.saveUserToSession(user, session);
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
						user.setLoginPass(MD5Util.getValue(MD5Util.PREFIX + newPass.trim()));
						userService.update(user);
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
				user.setLoginPass(MD5Util.getValue(MD5Util.PREFIX + newPass.trim()));
				userService.update(user);
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