package me.lb.service.system.impl;

import java.util.List;

import me.lb.dao.system.PermDao;
import me.lb.model.system.Perm;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.system.PermService;

import org.springframework.stereotype.Service;

@Service
public class PermServiceImpl extends GenericServiceImpl<Perm> implements
		PermService {

	@Override
	public List<Perm> findTopPerms() {
		return ((PermDao) dao).findTopPerms();
	}

	@Override
	public List<Perm> findByRoleId(int roleId) {
		return ((PermDao) dao).findByRoleId(roleId);
	}

}