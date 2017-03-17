package me.lb.service.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lb.dao.system.UserDao;
import me.lb.model.system.User;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.system.UserService;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 注意：由于系统和Activiti使用了两套不同的用户-角色（组）架构，所以这里需要将系统的数据同步到Activiti中
 * UserServiceImpl
 * @author lanbin
 * @date 2017-2-7
 */
@Service
public class UserServiceImpl extends GenericServiceImpl<User> implements
		UserService {

	@Autowired
	private IdentityService identityService;

	@Override
	public int save(User obj) {
		// 先存储系统的用户
		int id = super.save(obj);
		if (id > 0) {
			org.activiti.engine.identity.User user = identityService
					.newUser(String.valueOf(id));
			user.setLastName(obj.getLoginName());
			user.setPassword(obj.getLoginPass());
			identityService.saveUser(user);
		}
		return id;
	}

	@Override
	public void update(int id, User obj) {
		// 先更新系统的用户
		super.update(id, obj);
		// 处理Activiti的用户
		org.activiti.engine.identity.User user = identityService
				.createUserQuery().userId(String.valueOf(obj.getId()))
				.singleResult();
		if (user != null) {
			user.setLastName(obj.getLoginName());
			user.setPassword(obj.getLoginPass());
			identityService.saveUser(user);
		}
	}

	@Override
	public void delete(int id) {
		// 先删除Activiti的用户
		if (id > 0) {
			identityService.deleteUser(String.valueOf(id));
		}
		// 后处理系统用户
		super.delete(id);
	}

	@Override
	public void deleteAll() {
		List<User> users = findAll();
		for (User user : users) {
			// 先删除Activiti的用户
			if (user.getId() > 0) {
				identityService.deleteUser(String.valueOf(user.getId()));
			}
		}
		// 后处理系统用户
		super.deleteAll();
	}

	@Override
	public void deleteAll(List<Integer> ids) {
		for (int id : ids) {
			// 先删除Activiti的用户
			if (id > 0) {
				identityService.deleteUser(String.valueOf(id));
			}
		}
		// 后处理系统用户
		super.deleteAll(ids);
	}

	@Override
	public boolean validate(String loginName) {
		User temp = findByLoginName(loginName);
		if (temp == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public User findByLoginName(String loginName) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginName", loginName);
		List<User> temp = ((UserDao) dao).findAll(params);
		if (!temp.isEmpty()) {
			return temp.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void auth(int userId, List<Integer> roleIds) {
		// TODO: 调整为用户分配角色的方法

		// 将用户-角色的关系同步到Activiti的用户-组关系上
		// 每次都是先将所有的旧数据清除
		List<Group> groups = identityService.createGroupQuery()
				.groupMember(String.valueOf(userId)).list();
		for (Group group : groups) {
			identityService.deleteMembership(String.valueOf(userId),
					group.getId());
		}
		// 再存储新的数据
		for (int roleId : roleIds) {
			identityService.createMembership(String.valueOf(userId),
					String.valueOf(roleId));
		}
	}

}