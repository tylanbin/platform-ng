package me.lb.dao.system.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.UserDao;
import me.lb.model.pagination.Pagination;
import me.lb.model.system.User;

@Repository
public class UserDaoImpl extends GenericDaoImpl<User, Integer> implements
		UserDao {

	@Override
	public Pagination<User> pagingQuery() {
		return getPagination("from User", null);
	}

	@Override
	public Pagination<User> pagingQuery(Map<String, Object> params) {
		// 不使用的话可以不实现
		return null;
	}

}