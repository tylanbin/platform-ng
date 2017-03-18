package me.lb.dao.system;

import java.util.List;

import me.lb.dao.common.GenericDao;
import me.lb.model.system.Role;

public interface RoleDao extends GenericDao<Role> {

	/**
	 * 查询用户的所有角色
	 * @param userId 用户id
	 * @return 角色集合
	 */
	public List<Role> findByUserId(int userId);

}