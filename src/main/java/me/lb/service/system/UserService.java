package me.lb.service.system;

import me.lb.model.system.User;
import me.lb.service.common.GenericService;

public interface UserService extends GenericService<User, Integer> {

	public boolean validateLoginName(String loginName);

	public User findByLoginName(String loginName);

}