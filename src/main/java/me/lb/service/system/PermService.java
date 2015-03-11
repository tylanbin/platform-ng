package me.lb.service.system;

import java.util.List;

import me.lb.model.system.Perm;
import me.lb.service.common.GenericService;

public interface PermService extends GenericService<Perm, Integer> {

	/**
	 * 查询全部的顶级资源（没有父id）
	 */
	public List<Perm> findTopPerms();

}