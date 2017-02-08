package me.lb.dao.system.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.UserDao;
import me.lb.model.pagination.Pagination;
import me.lb.model.system.User;

import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl extends GenericDaoImpl<User, Integer> implements
		UserDao {

	@Override
	public Pagination<User> pagingQuery() {
		return getPagination("from User", null);
	}

	@Override
	public Pagination<User> pagingQuery(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer("from User as o where 1=1");
		List<Object> objs = new ArrayList<Object>();
		Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> me = it.next();
			// 特殊处理Like
			if (me.getKey().endsWith("Like")) {
				sb.append(" and o." + me.getKey().substring(0, me.getKey().length() - 4) + " like ?");
				objs.add("%" + me.getValue() + "%");
			} else {
				sb.append(" and o." + me.getKey() + " = ?");
				objs.add(me.getValue());
			}
		}
		return getPagination(sb.toString(), objs);
	}

}