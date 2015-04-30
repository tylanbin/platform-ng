package me.lb.dao.system.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.EmpDao;
import me.lb.model.pagination.Pagination;
import me.lb.model.system.Emp;

import org.springframework.stereotype.Repository;

@Repository
public class EmpDaoImpl extends GenericDaoImpl<Emp, Integer> implements EmpDao {

	@Override
	public Pagination<Emp> pagingQuery() {
		return getPagination("from Emp", null);
	}

	@Override
	public Pagination<Emp> pagingQuery(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer("from Emp as o where 1=1");
		List<Object> objs = new ArrayList<Object>();
		Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> me = it.next();
			// 需要额外处理模糊查询的参数
			if ("name".equals(me.getKey()) || "job".equals(me.getKey())
					|| "contact".equals(me.getKey())
					|| "idCard".equals(me.getKey())) {
				sb.append(" and o." + me.getKey() + " like ?");
				objs.add("%" + me.getValue() + "%");
			} else if ("org.id".equals(me.getKey())) {
				// 所属机构id需要特殊处理非空
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

}