package me.lb.dao.system;

import java.util.List;

import me.lb.dao.common.GenericDao;
import me.lb.model.system.Perm;

public interface PermDao extends GenericDao<Perm> {

	/**
	 * 级联查询全部的顶级资源（没有父id）（树状）
	 */
	public List<Perm> findTopPerms();

}