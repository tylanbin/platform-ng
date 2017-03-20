package me.lb.service.system;

import java.util.List;
import java.util.Map;

import me.lb.model.system.User;
import me.lb.service.common.GenericService;

public interface UserService extends GenericService<User> {

	/**
	 * 验证用户名可否使用
	 * @param loginName 用户名
	 * @return true-可使用/false-重名
	 */
	public boolean validate(String loginName);

	/**
	 * 根据用户名查询用户
	 * @param loginName 用户名
	 * @return 用户
	 */
	public User findByLoginName(String loginName);

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
	
	/**
	 * 为用户一次分配多个角色
	 * @param userId 角色id
	 * @param roleIds 权限id集合
	 */
	public void auth(int userId, List<Integer> roleIds);

}