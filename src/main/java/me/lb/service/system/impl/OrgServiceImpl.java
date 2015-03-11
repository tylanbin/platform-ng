package me.lb.service.system.impl;

import java.util.List;

import me.lb.dao.system.OrgDao;
import me.lb.model.system.Org;
import me.lb.service.common.impl.GenericServiceImpl;
import me.lb.service.system.OrgService;

import org.springframework.stereotype.Service;

@Service
public class OrgServiceImpl extends GenericServiceImpl<Org, Integer> implements
		OrgService {

	@Override
	public List<Org> findTopOrgs() {
		return ((OrgDao) dao).findTopOrgs();
	}

}