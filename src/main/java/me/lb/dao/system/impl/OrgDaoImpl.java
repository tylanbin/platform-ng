package me.lb.dao.system.impl;

import java.util.List;

import me.lb.dao.common.impl.GenericDaoImpl;
import me.lb.dao.system.OrgDao;
import me.lb.model.system.Org;

import org.springframework.stereotype.Repository;

@Repository
public class OrgDaoImpl extends GenericDaoImpl<Org> implements OrgDao {

	@Override
	protected String getTableName() {
		// 设置使用的表名
		return "ng_sys_org";
	}

	@Override
	public List<Org> findTopOrgs() {
		return null;
	}

}