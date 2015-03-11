package me.lb.service.system;

import java.util.List;

import me.lb.model.system.Role;
import me.lb.service.common.GenericService;

public interface RoleService extends GenericService<Role, Integer> {

	/**
	 * 查询全部的顶级角色（没有父id）
	 */
	public List<Role> findTopRoles();

}