package me.lb.dao.system.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.OrgDao;
import me.lb.model.pagination.Pagination;
import me.lb.model.system.Org;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class OrgDaoImpl extends GenericDaoImpl<Org, Integer> implements OrgDao {

	@Override
	public Pagination<Org> pagingQuery() {
		return getPagination("from Org", null);
	}

	@Override
	public Pagination<Org> pagingQuery(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer("from Org as o where 1=1");
		List<Object> objs = new ArrayList<Object>();
		Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> me = it.next();
			// 特殊处理Like
			if (me.getKey().endsWith("Like")) {
				sb.append(" and o." + me.getKey().substring(0, me.getKey().length() - 4) + " like ?");
				objs.add("%" + me.getValue() + "%");
			} else if ("org.id".equals(me.getKey())) {
				// 父对象id需要特殊处理非空
				if ((Integer) me.getValue() == -1) {
					sb.append(" and o.org is null");
				} else {
					sb.append(" and o.org.id = ?");
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
	public List<Org> findTopOrgs() {
		StringBuffer sb = new StringBuffer("from Org as o");
		sb.append(" where o.org is null");
		Query q = sessionFactory.getCurrentSession().createQuery(sb.toString());
		return q.list();
	}

}