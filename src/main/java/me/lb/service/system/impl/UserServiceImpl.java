package me.lb.service.system.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.lb.dao.system.UserDao;
import me.lb.model.system.Role;
import me.lb.model.system.User;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.system.UserService;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends GenericServiceImpl<User, Integer>
		implements UserService {

	@Override
	public boolean validate(String loginName) {
		User temp = findByLoginName(loginName);
		if (temp == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public User findByLoginName(String loginName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginName", loginName);
		List<User> temp = ((UserDao) dao).findAll(params);
		if (temp != null && temp.size() > 0) {
			return temp.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void auth(int userId, List<Integer> roleIds) {
		// 由于cascade方式只级联删除操作，所以这里可以通过欺骗的方式提升效率
		Set<Role> roles = new HashSet<Role>();
		for (int roleId : roleIds) {
			// 这里只要构建数据库中存在id的对象即可，避免了查询的开销
			Role role = new Role();
			role.setId(roleId);
			roles.add(role);
		}
		// 查询用户信息，更新关联（直接更新）
		User user = dao.findById(userId);
		user.setRoles(roles);
		dao.update(user);

	}

}