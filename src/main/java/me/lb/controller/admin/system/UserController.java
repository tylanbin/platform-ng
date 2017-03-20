package me.lb.controller.admin.system;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lb.model.system.Role;
import me.lb.model.system.User;
import me.lb.service.system.RoleService;
import me.lb.service.system.UserService;
import me.lb.support.jackson.JsonWriter;
import me.lb.utils.MD5Util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
// 依附于员工实现
@RequestMapping(value = "/admin/system/emp")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;

	@ResponseBody
	@RequestMapping(value = "/{empId}/user", method = RequestMethod.POST)
	public String user(@PathVariable int empId, String objs) {
		try {
			ObjectMapper om = new ObjectMapper();
			List<User> list = om.readValue(objs, new TypeReference<List<User>>() {});
			// 先完成验证
			for (User temp : list) {
				if (temp.getId() == 0) {
					// 未存储的新用户才需要验证
					String loginName = temp.getLoginName();
					if (!userService.validate(loginName)) {
						return "{ \"msg\" : \"" + loginName + "与已有用户名冲突，请更换后重试！\" }";
					}
				}
			}
			// 通过验证
			for (User temp : list) {
				// id判断是否存在
				int userId = 0;
				if (temp.getId() > 0) {
					// id存在，有该记录，更新
					// 为了避免更新导致的清空数据，仅处理需要更新的字段
					userId = temp.getId();
					User obj = userService.findById(temp.getId());
					obj.setLoginName(temp.getLoginName().trim());
					obj.setEnabled(temp.getEnabled());
					// 使用自定义的MD5进行编码
					if (obj.getLoginPass().equals(temp.getLoginPass().trim())) {
						// 如果密码相同，避免再次进行MD5编码
					} else {
						String md5Pass = MD5Util.getValue(MD5Util.PREFIX + temp.getLoginPass().trim());
						obj.setLoginPass(md5Pass);
					}
					userService.update(userId, obj);
				} else {
					// id不存在，需要存储
					temp.setEmpId(empId);
					temp.setLoginName(temp.getLoginName().trim());
					temp.setCreateDate(new Timestamp(new Date().getTime()));
					// 使用自定义的MD5进行编码
					String md5Pass = MD5Util.getValue(MD5Util.PREFIX + temp.getLoginPass().trim());
					temp.setLoginPass(md5Pass);
					userId = userService.save(temp);
				}
				// 再处理与角色的关联
				if (!StringUtils.isEmpty(temp.getRoleIds())) {
					List<Integer> roleIds = om.readValue("[" + temp.getRoleIds() + "]", new TypeReference<List<Integer>>() {});
					userService.auth(userId, roleIds);
				}
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"分配失败，请确认操作无误后重试，或联系管理员处理！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{empId}/user/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable int empId, @PathVariable int id) {
		try {
			userService.delete(id);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"删除失败，请确认操作无误后重试，或联系管理员处理！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{empId}/user/data", method = RequestMethod.GET)
	public String data(@PathVariable int empId) throws Exception {
		try {
			// 用于展示某个员工用户列表的查询
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("empId", empId);
			List<User> users = userService.findAll(params);
			for (User user : users) {
				// 处理用户的角色
				List<Role> roles = roleService.findByUserId(user.getId());
				String temp = roles.toString().replaceAll(" ", "");
				user.setRoleIds(temp.substring(1, temp.length() - 1));
			}
			return JsonWriter.getInstance().filter(User.class)
					.getWriter().writeValueAsString(users);
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		}
	}

}
