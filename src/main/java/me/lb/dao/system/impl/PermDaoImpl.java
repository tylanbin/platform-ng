package me.lb.dao.system.impl;

import java.util.List;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.PermDao;
import me.lb.model.system.Perm;

import org.springframework.stereotype.Repository;

@Repository
public class PermDaoImpl extends GenericDaoImpl<Perm> implements PermDao {
	
	private static final String PKG = "me.lb.model.system.Perm.";

	@Override
	protected String getTableName() {
		// 设置使用的表名
		return "ng_sys_perm";
	}
	
	@Override
	protected String[] getIgnored() {
		return new String[] { "text", "children" };
	}

	@Override
	public List<Perm> findTopPerms() {
		return sqlSessionTemplate.selectList(PKG + "findTops");
	}

	@Override
	public List<Perm> findByRoleId(int roleId) {
		return sqlSessionTemplate.selectList(PKG + "findByRoleId", roleId);
	}

}