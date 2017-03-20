package me.lb.dao.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.RoleDao;
import me.lb.model.system.Role;

import org.springframework.stereotype.Repository;

@Repository
public class RoleDaoImpl extends GenericDaoImpl<Role> implements RoleDao {
	
	private static final String PKG = "me.lb.model.system.Role.";

	@Override
	protected String getTableName() {
		// 设置使用的表名
		return "ng_sys_role";
	}
	
	@Override
	protected String[] getIgnored() {
		return new String[0];
	}

	@Override
	public List<Role> findByUserId(int userId) {
		return sqlSessionTemplate.selectList(PKG + "findByUserId", userId);
	}

	@Override
	public List<Map<Integer, Integer>> findRolePerm(Integer roleId, Integer permId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleId", roleId);
		params.put("permId", permId);
		return sqlSessionTemplate.selectList(PKG + "findRolePerm", params);
	}

	@Override
	public void saveRolePerm(int roleId, int permId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleId", roleId);
		params.put("permId", permId);
		sqlSessionTemplate.selectList(PKG + "saveRolePerm", params);
	}

	@Override
	public void deleteRolePerm(Integer roleId, Integer permId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("roleId", roleId);
		params.put("permId", permId);
		sqlSessionTemplate.selectList(PKG + "deleteRolePerm", params);
	}

}