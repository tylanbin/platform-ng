package me.lb.service.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lb.dao.system.UserDao;
import me.lb.model.system.User;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.system.UserService;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends GenericServiceImpl<User, Integer>
		implements UserService {

	@Override
	public boolean validateLoginName(String loginName) {
		return false;
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

}