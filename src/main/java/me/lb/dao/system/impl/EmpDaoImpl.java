package me.lb.dao.system.impl;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.EmpDao;
import me.lb.model.system.Emp;

import org.springframework.stereotype.Repository;

@Repository
public class EmpDaoImpl extends GenericDaoImpl<Emp> implements EmpDao {

	@Override
	protected String getTableName() {
		// 设置使用的表名
		return "ng_sys_emp";
	}
	
	@Override
	protected String[] getIgnored() {
		return new String[0];
	}

}