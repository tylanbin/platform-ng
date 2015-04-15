package me.lb.service.system;

import me.lb.model.system.User;
import me.lb.service.common.GenericService;

public interface UserService extends GenericService<User, Integer> {

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

}