package me.lb.dao.system;

import java.util.List;

import me.lb.dao.common.GenericDao;
import me.lb.model.system.Perm;

public interface PermDao extends GenericDao<Perm, Integer> {

	/**
	 * 查询全部的顶级资源（没有父id）
	 */
	public List<Perm> findTopPerms();

}