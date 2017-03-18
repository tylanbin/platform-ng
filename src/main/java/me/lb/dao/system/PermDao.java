package me.lb.dao.system;

import java.util.List;

import me.lb.dao.common.GenericDao;
import me.lb.model.system.Perm;

public interface PermDao extends GenericDao<Perm> {

	/**
	 * 级联查询全部的顶级资源（没有父id）（树状）
	 * @return 权限集合
	 */
	public List<Perm> findTopPerms();
	
	/**
	 * 查询岗位下包含的所有权限
	 * @param roleId 角色id
	 * @return 权限集合
	 */
	public List<Perm> findByRoleId(int roleId);

}