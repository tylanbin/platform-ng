package me.lb.dao.demo.impl;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import org.springframework.stereotype.Repository;
import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.demo.FooDao;
import me.lb.model.pagination.Pagination;
import me.lb.model.demo.Foo;

@Repository
public class FooDaoImpl extends GenericDaoImpl<Foo, Integer> implements FooDao {

	@Override
	public Pagination<Foo> pagingQuery() {
		return getPagination("from Foo", null);
	}

	@Override
	public Pagination<Foo> pagingQuery(Map<String, Object> params) {
		// 不使用的话可以不实现
		StringBuffer sb = new StringBuffer("from Foo as o where 1=1");
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
