package me.lb.service.system.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.lb.dao.system.RoleDao;
import me.lb.model.system.Perm;
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
public class RoleServiceImpl extends GenericServiceImpl<Role, Integer>
		implements RoleService {
	
	@Autowired
	private IdentityService identityService;

	@Override
	public Integer save(Role entity) {
		// 先存储系统的角色
		int id = super.save(entity);
		if (id > 0) {
			org.activiti.engine.identity.Group group = identityService.newGroup(String.valueOf(id));
			group.setName(entity.getName());
			// group.setType(type); 先留空类型
			identityService.saveGroup(group);
		}
		return id;
	}

	@Override
	public void update(Role entity) {
		// 先更新系统的角色
		super.update(entity);
		// 处理Activiti的组
		org.activiti.engine.identity.Group group = identityService.createGroupQuery().groupId(String.valueOf(entity.getId())).singleResult();
		if (group != null) {
			group.setName(entity.getName());
			// group.setType(type); 先留空类型
			identityService.saveGroup(group);
		}
	}

	@Override
	public void delete(Role entity) {
		// 先删除Activiti的组
		if (entity.getId() != null && entity.getId() > 0) {
			identityService.deleteGroup(String.valueOf(entity.getId()));
		}
		// 后处理系统角色
		super.delete(entity);
	}

	@Override
	public void deleteAll() {
		List<Role> roles = findAll();
		for (Role role : roles) {
			// 先删除Activiti的组
			if (role.getId() != null && role.getId() > 0) {
				identityService.deleteGroup(String.valueOf(role.getId()));
			}
		}
		// 后处理系统角色
		super.deleteAll();
	}

	@Override
	public void deleteAll(Collection<Role> entities) {
		for (Role role : entities) {
			// 先删除Activiti的组
			if (role.getId() != null && role.getId() > 0) {
				identityService.deleteGroup(String.valueOf(role.getId()));
			}
		}
		// 后处理系统角色
		super.deleteAll(entities);
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
		if (temp != null && temp.size() > 0) {
			return temp.get(0);
		} else {
			return null;
		}
	}

	@Override
	public void auth(int roleId, List<Integer> permIds) {
		// 由于cascade方式只级联删除操作，所以这里可以通过欺骗的方式提升效率
		Set<Perm> perms = new HashSet<Perm>();
		for (int permId : permIds) {
			// 这里只要构建数据库中存在id的对象即可，避免了查询的开销
			Perm perm = new Perm();
			perm.setId(permId);
			perms.add(perm);
		}
		// 查询角色信息，更新关联（直接更新）
		Role role = dao.findById(roleId);
		role.setPerms(perms);
		dao.update(role);
	}

}