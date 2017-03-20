package me.lb.dao.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.UserDao;
import me.lb.model.system.User;

import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends GenericDaoImpl<User> implements UserDao {
	
	private static final String PKG = "me.lb.model.system.User.";

	@Override
	protected String getTableName() {
		// 设置使用的表名
		return "ng_sys_user";
	}
	
	@Override
	protected String[] getIgnored() {
		return new String[0];
	}

	@Override
	public List<Map<Integer, Integer>> findUserRole(Integer userId, Integer roleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("roleId", roleId);
		return sqlSessionTemplate.selectList(PKG + "findUserRole", params);
	}

	@Override
	public void saveUserRole(int userId, int roleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("roleId", roleId);
		sqlSessionTemplate.selectList(PKG + "saveUserRole", params);
	}

	@Override
	public void deleteUserRole(Integer userId, Integer roleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("roleId", roleId);
		sqlSessionTemplate.selectList(PKG + "deleteUserRole", params);
	}

}