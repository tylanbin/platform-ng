package me.lb.service.system;

import java.util.List;

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
	 * 为角色一次授予多个权限
	 * @param roleId 角色id
	 * @param permIds 权限id集合
	 */
	public void auth(int roleId, List<Integer> permIds);

}