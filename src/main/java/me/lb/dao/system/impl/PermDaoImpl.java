package me.lb.dao.system.impl;

import java.util.List;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.PermDao;
import me.lb.model.system.Perm;

import org.springframework.stereotype.Repository;

@Repository
public class PermDaoImpl extends GenericDaoImpl<Perm> implements PermDao {

	@Override
	protected String getTableName() {
		// 设置使用的表名
		return "ng_sys_perm";
	}

	@Override
	public List<Perm> findTopPerms() {
		return null;
	}

}