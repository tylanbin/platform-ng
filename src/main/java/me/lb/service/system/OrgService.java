package me.lb.service.system;

import java.util.List;

import me.lb.model.system.Org;
import me.lb.service.common.GenericService;

public interface OrgService extends GenericService<Org, Integer> {

	/**
	 * 查询全部的顶级机构（没有父id）
	 */
	public List<Org> findTopOrgs();
	
}