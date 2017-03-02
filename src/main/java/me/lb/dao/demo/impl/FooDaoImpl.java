package me.lb.dao.demo.impl;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.demo.FooDao;
import me.lb.model.demo.Foo;

import org.springframework.stereotype.Repository;

@Repository
public class FooDaoImpl extends GenericDaoImpl<Foo> implements FooDao {

	@Override
	protected String getTableName() {
		return "ng_demo_foo";
	}

}
