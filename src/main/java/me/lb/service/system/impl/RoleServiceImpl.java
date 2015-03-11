package me.lb.service.system.impl;

import java.util.List;

import me.lb.dao.system.RoleDao;
import me.lb.model.system.Role;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.system.RoleService;

import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends GenericServiceImpl<Role, Integer>
		implements RoleService {

	@Override
	public List<Role> findTopRoles() {
		return ((RoleDao) dao).findTopRoles();
	}

}