package me.lb.dao.system;

import java.util.List;
import java.util.Map;

import me.lb.dao.common.GenericDao;
import me.lb.model.system.User;

public interface UserDao extends GenericDao<User> {

	// 处理中间表的方法

	/**
	 * 查询所有的用户、角色对应关系
	 * @param userId 用户id（可为空）
	 * @param roleId 角色id（可为空）
	 * @return 用户id->角色id的map
	 */
	public List<Map<Integer, Integer>> findUserRole(Integer userId, Integer roleId);

	/**
	 * 存储一个用户和角色的对应关系
	 * @param userId 用户id
	 * @param roleId 角色id
	 */
	public void saveUserRole(int userId, int roleId);

	/**
	 * 删除用户和角色的对应关系
	 * @param userId 用户id（可为空）
	 * @param roleId 角色id（可为空）
	 */
	public void deleteUserRole(Integer userId, Integer roleId);

}