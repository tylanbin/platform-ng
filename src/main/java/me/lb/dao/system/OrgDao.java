package me.lb.dao.system;

import java.util.List;

import me.lb.dao.common.GenericDao;
import me.lb.model.system.Org;

public interface OrgDao extends GenericDao<Org, Integer> {

	/**
	 * 查询全部的顶级机构（没有父id）
	 */
	public List<Org> findTopOrgs();

}