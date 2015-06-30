package me.lb.dao.demo.impl;

import java.util.Map;
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
		return null;
	}

}
