package me.lb.service.system;

import java.util.List;

import me.lb.model.system.Perm;
import me.lb.service.common.GenericService;

public interface PermService extends GenericService<Perm> {

	/**
	 * 查询全部的顶级资源（没有父id）
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