package me.lb.dao.system.impl;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.UserDao;
import me.lb.model.system.User;

import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends GenericDaoImpl<User> implements UserDao {

	@Override
	protected String getTableName() {
		// 设置使用的表名
		return "ng_sys_user";
	}
	
	@Override
	protected String[] getIgnored() {
		return new String[0];
	}

}