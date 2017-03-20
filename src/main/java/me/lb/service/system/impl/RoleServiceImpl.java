package me.lb.service.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lb.dao.system.RoleDao;
import me.lb.model.system.Role;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.system.RoleService;

import org.activiti.engine.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 注意：由于系统和Activiti使用了两套不同的用户-角色（组）架构，所以这里需要将系统的数据同步到Activiti中
 * RoleServiceImpl
 * @author lanbin
 * @update 2017-2-7
 */
@Service
public class RoleServiceImpl extends GenericServiceImpl<Role> implements
		RoleService {

	@Autowired
	private IdentityService identityService;

	@Override
	public int save(Role obj) {
		// 先存储系统的角色
		int id = super.save(obj);
		if (id > 0) {
			org.activiti.engine.identity.Group group = identityService
					.newGroup(String.valueOf(id));
			group.setName(obj.getName());
			// group.setType(type); 先留空类型
			identityService.saveGroup(group);
		}
		return id;
	}

	@Override
	public void update(int id, Role obj) {
		// 先更新系统的角色
		super.update(id, obj);
		// 处理Activiti的组
		org.activiti.engine.identity.Group group = identityService
				.createGroupQuery().groupId(String.valueOf(obj.getId()))
				.singleResult();
		if (group != null) {
			group.setName(obj.getName());
			// group.setType(type); 先留空类型
			identityService.saveGroup(group);
		}
	}

	@Override
	public void delete(int id) {
		// 先删除Activiti的组
		if (id > 0) {
			identityService.deleteGroup(String.valueOf(id));
		}
		// 后处理系统角色
		super.delete(id);
	}

	@Override
	public void deleteAll() {
		List<Role> roles = findAll();
		for (Role role : roles) {
			// 先删除Activiti的组
			if (role.getId() > 0) {
				identityService.deleteGroup(String.valueOf(role.getId()));
			}
		}
		// 后处理系统角色
		super.deleteAll();
	}

	@Override
	public void deleteAll(List<Integer> ids) {
		for (int id : ids) {
			// 先删除Activiti的组
			if (id > 0) {
				identityService.deleteGroup(String.valueOf(id));
			}
		}
		// 后处理系统角色
		super.deleteAll(ids);
	}

	@Override
	public boolean validate(String name) {
		Role temp = findByName(name);
		if (temp == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Role findByName(String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		List<Role> temp = ((RoleDao) dao).findAll(params);
		if (!temp.isEmpty()) {
			return temp.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public List<Role> findByUserId(int userId) {
		return ((RoleDao) dao).findByUserId(userId);
	}

	@Override
	public List<Map<Integer, Integer>> findRolePerm(Integer roleId, Integer permId) {
		return ((RoleDao) dao).findRolePerm(roleId, permId);
	}

	@Override
	public void saveRolePerm(int roleId, int permId) {
		((RoleDao) dao).saveRolePerm(roleId, permId);
	}

	@Override
	public void deleteRolePerm(Integer roleId, Integer permId) {
		((RoleDao) dao).deleteRolePerm(roleId, permId);
	}

	@Override
	public void auth(int roleId, List<Integer> permIds) {
		// 为角色授权，首先先将角色所有的旧权限删除
		((RoleDao) dao).deleteRolePerm(roleId, null);
		// 然后将新的权限循环存储即可
		for (int permId : permIds) {
			((RoleDao) dao).saveRolePerm(roleId, permId);
		}
	}

}