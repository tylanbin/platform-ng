package me.lb.service.system.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.lb.model.system.Perm;
import me.lb.model.system.Role;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.system.RoleService;

import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends GenericServiceImpl<Role, Integer>
		implements RoleService {

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