package me.lb.dao.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.model.pagination.Pagination;
import me.lb.model.system.User;

import org.springframework.stereotype.Repository;

@Repository
public class CommonDaoImpl extends GenericDaoImpl<User, Integer> implements
		CommonDao {

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
			// 需要额外处理模糊查询的参数
			// if ("orLogic".equals(me.getKey())) {
			// sb.append(" or o." + me.getKey() + " like ?");
			// objs.add("%" + me.getValue() + "%");
			// } else {
			sb.append(" and o." + me.getKey() + " = ?");
			objs.add(me.getValue());
			// }
		}
		return getPagination(sb.toString(), objs);
	}

}