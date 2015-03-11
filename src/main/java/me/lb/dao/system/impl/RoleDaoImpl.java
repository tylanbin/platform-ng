package me.lb.dao.system.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.RoleDao;
import me.lb.model.pagination.Pagination;
import me.lb.model.system.Role;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDaoImpl extends GenericDaoImpl<Role, Integer> implements
		RoleDao {

	@Override
	public Pagination<Role> pagingQuery() {
		return getPagination("from Role", null);
	}

	@Override
	public Pagination<Role> pagingQuery(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer("from Perm as o where 1=1");
		List<Object> objs = new ArrayList<Object>();
		Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> me = it.next();
			// 需要额外处理模糊查询的参数
			if ("name".equals(me.getKey())) {
				sb.append(" and o." + me.getKey() + " = ?");
				objs.add("%" + me.getValue() + "%");
			} else {
				sb.append(" and o." + me.getKey() + " = ?");
				objs.add(me.getValue());
			}
		}
		return getPagination(sb.toString(), objs);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Role> findTopRoles() {
		StringBuffer sb = new StringBuffer("from Role as o");
		sb.append(" where o.role is null");
		Query q = sessionFactory.getCurrentSession().createQuery(sb.toString());
		return q.list();
	}

}