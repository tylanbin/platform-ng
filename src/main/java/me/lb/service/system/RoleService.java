package me.lb.service.system;

import java.util.List;
import java.util.Map;

import me.lb.model.system.Role;
import me.lb.service.common.GenericService;

public interface RoleService extends GenericService<Role> {

	/**
	 * 验证角色名可否使用
	 * @param name 角色名
	 * @return true-可使用/false-重名
	 */
	public boolean validate(String name);

	/**
	 * 根据角色名查询角色
	 * @param name 角色名
	 * @return 角色
	 */
	public Role findByName(String name);

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
	
	/**
	 * 为角色一次授予多个权限
	 * @param roleId 角色id
	 * @param permIds 权限id集合
	 */
	public void auth(int roleId, List<Integer> permIds);

}