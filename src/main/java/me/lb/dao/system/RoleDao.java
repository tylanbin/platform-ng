package me.lb.dao.system;

import java.util.List;
import java.util.Map;

import me.lb.dao.common.GenericDao;
import me.lb.model.system.Role;

public interface RoleDao extends GenericDao<Role> {

	/**
	 * 查询用户的所有角色
	 * @param userId 用户id
	 * @return 角色集合
	 */
	public List<Role> findByUserId(int userId);
	
	// 处理中间表的方法
	
	/**
	 * 查询所有的角色、权限对应关系
	 * @param roleId 角色id（可为空）
	 * @param permId 权限id（可为空）
	 * @return 角色id->权限id的map
	 */
	public List<Map<Integer, Integer>> findRolePerm(Integer roleId, Integer permId);
	
	/**
	 * 存储一个角色和权限的对应关系
	 * @param roleId 角色id
	 * @param permId 权限id
	 */
	public void saveRolePerm(int roleId, int permId);
	
	/**
	 * 删除角色和权限的对应关系
	 * @param roleId 角色id（可为空）
	 * @param permId 权限id（可为空）
	 */
	public void deleteRolePerm(Integer roleId, Integer permId);

}