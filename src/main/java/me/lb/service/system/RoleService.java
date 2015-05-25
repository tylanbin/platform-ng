package me.lb.service.system;

import java.util.List;

import me.lb.model.system.Role;
import me.lb.service.common.GenericService;

public interface RoleService extends GenericService<Role, Integer> {

	/**
	 * 为角色一次授予多个权限
	 * @param roleId 角色id
	 * @param permIds 权限id集合
	 */
	public void auth(int roleId, List<Integer> permIds);

}