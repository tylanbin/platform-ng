package me.lb.service.common.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import me.lb.dao.common.GenericDao;
import me.lb.model.pagination.Pagination;
import me.lb.service.common.GenericService;

public class GenericServiceImpl<T, PK extends Serializable> implements
		GenericService<T, PK> {

	@Autowired
	protected GenericDao<T, PK> dao;

	@Override
	public T findById(PK id) {
		return dao.findById(id);
	}

	@Override
	public List<T> findAll() {
		return dao.findAll();
	}

	@Override
	public List<T> findAll(Map<String, Object> params) {
		return dao.findAll(params);
	}

	@Override
	public PK save(T entity) {
		return dao.save(entity);
	}

	@Override
	public void update(T entity) {
		dao.update(entity);
	}

	@Override
	public void delete(T entity) {
		dao.delete(entity);
	}

	@Override
	public void deleteAll() {
		dao.deleteAll();
	}

	@Override
	public void deleteAll(Collection<T> entities) {
		dao.deleteAll(entities);
	}

	@Override
	public Pagination<T> pagingQuery() {
		return dao.pagingQuery();
	}

	@Override
	public Pagination<T> pagingQuery(Map<String, Object> params) {
		return dao.pagingQuery(params);
	}

}
