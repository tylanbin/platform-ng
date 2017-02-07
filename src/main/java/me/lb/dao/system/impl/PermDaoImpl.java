package me.lb.dao.system.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.PermDao;
import me.lb.model.pagination.Pagination;
import me.lb.model.system.Perm;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class PermDaoImpl extends GenericDaoImpl<Perm, Integer> implements
		PermDao {

	@Override
	public Pagination<Perm> pagingQuery() {
		return getPagination("from Perm", null);
	}

	@Override
	public Pagination<Perm> pagingQuery(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer("from Perm as o where 1=1");
		List<Object> objs = new ArrayList<Object>();
		Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> me = it.next();
			// 特殊处理Like
			if (me.getKey().endsWith("Like")) {
				sb.append(" and o." + me.getKey().substring(0, me.getKey().length() - 4) + " like ?");
				objs.add("%" + me.getValue() + "%");
			} else if ("perm.id".equals(me.getKey())) {
				// 父对象id需要特殊处理非空
				if ((Integer) me.getValue() == -1) {
					sb.append(" and o.perm is null");
				} else {
					sb.append(" and o.perm.id = ?");
					objs.add(me.getValue());
				}
			} else {
				sb.append(" and o." + me.getKey() + " = ?");
				objs.add(me.getValue());
			}
		}
		return getPagination(sb.toString(), objs);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Perm> findTopPerms() {
		StringBuffer sb = new StringBuffer("from Perm as o");
		sb.append(" where o.perm is null");
		Query q = sessionFactory.getCurrentSession().createQuery(sb.toString());
		return q.list();
	}

}