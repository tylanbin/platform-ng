package me.lb.service.system.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.lb.dao.system.UserDao;
import me.lb.model.system.Role;
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
public class UserServiceImpl extends GenericServiceImpl<User, Integer>
		implements UserService {
	
	@Autowired
	private IdentityService identityService;

	@Override
	public Integer save(User entity) {
		// 先存储系统的用户
		int id = super.save(entity);
		if (id > 0) {
			org.activiti.engine.identity.User user = identityService.newUser(String.valueOf(id));
			user.setLastName(entity.getLoginName());
			user.setPassword(entity.getLoginPass());
			identityService.saveUser(user);
		}
		return id;
	}

	@Override
	public void update(User entity) {
		// 先更新系统的用户
		super.update(entity);
		// 处理Activiti的用户
		org.activiti.engine.identity.User user = identityService.createUserQuery().userId(String.valueOf(entity.getId())).singleResult();
		if (user != null) {
			user.setLastName(entity.getLoginName());
			user.setPassword(entity.getLoginPass());
			identityService.saveUser(user);
		}
	}

	@Override
	public void delete(User entity) {
		// 先删除Activiti的用户
		if (entity.getId() != null && entity.getId() > 0) {
			identityService.deleteUser(String.valueOf(entity.getId()));
		}
		// 后处理系统用户
		super.delete(entity);
	}

	@Override
	public void deleteAll() {
		List<User> users = findAll();
		for (User user : users) {
			// 先删除Activiti的用户
			if (user.getId() != null && user.getId() > 0) {
				identityService.deleteUser(String.valueOf(user.getId()));
			}
		}
		// 后处理系统用户
		super.deleteAll();
	}

	@Override
	public void deleteAll(Collection<User> entities) {
		for (User user : entities) {
			// 先删除Activiti的用户
			if (user.getId() != null && user.getId() > 0) {
				identityService.deleteUser(String.valueOf(user.getId()));
			}
		}
		// 后处理系统用户
		super.deleteAll(entities);
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
		if (temp != null && temp.size() > 0) {
			return temp.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void auth(int userId, List<Integer> roleIds) {
		// 由于cascade方式只级联删除操作，所以这里可以通过欺骗的方式提升效率
		Set<Role> roles = new HashSet<Role>();
		for (int roleId : roleIds) {
			// 这里只要构建数据库中存在id的对象即可，避免了查询的开销
			Role role = new Role();
			role.setId(roleId);
			roles.add(role);
		}
		// 查询用户信息，更新关联（直接更新）
		User user = dao.findById(userId);
		user.setRoles(roles);
		dao.update(user);
		// 将用户-角色的关系同步到Activiti的用户-组关系上
		// 每次都是先将所有的旧数据清除
		List<Group> groups = identityService.createGroupQuery().groupMember(String.valueOf(userId)).list();
        for (Group group : groups) {
            identityService.deleteMembership(String.valueOf(userId), group.getId());
        }
        // 再存储新的数据
        for (Role role : roles) {
            identityService.createMembership(String.valueOf(userId), String.valueOf(role.getId()));
        }
	}

}