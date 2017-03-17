package me.lb.dao.system.impl;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.RoleDao;
import me.lb.model.system.Role;

import org.springframework.stereotype.Repository;

@Repository
public class RoleDaoImpl extends GenericDaoImpl<Role> implements RoleDao {

	@Override
	protected String getTableName() {
		// 设置使用的表名
		return "ng_sys_role";
	}
	
	@Override
	protected String[] getIgnored() {
		return new String[0];
	}

}