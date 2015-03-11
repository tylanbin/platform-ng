package me.lb.dao.system;

import java.util.List;

import me.lb.dao.common.GenericDao;
import me.lb.model.system.Role;

public interface RoleDao extends GenericDao<Role, Integer> {

	/**
	 * 查询全部的顶级角色（没有父id）
	 */
	public List<Role> findTopRoles();

}