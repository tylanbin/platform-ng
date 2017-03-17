package me.lb.service.common.impl;

import java.util.List;
import java.util.Map;

import me.lb.dao.common.GenericDao;
import me.lb.model.pagination.Pagination;
import me.lb.service.common.GenericService;

import org.springframework.beans.factory.annotation.Autowired;

public class GenericServiceImpl<T> implements GenericService<T> {

	@Autowired
	protected GenericDao<T> dao;

	@Override
	public T findById(int id) {
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
	public int save(T obj) {
		return dao.save(obj);
	}

	@Override
	public void update(int id, T obj) {
		dao.update(id, obj);
	}

	@Override
	public void delete(int id) {
		dao.delete(id);
	}

	@Override
	public void deleteAll() {
		dao.deleteAll();
	}

	@Override
	public void deleteAll(List<Integer> ids) {
		dao.deleteAll(ids);
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
