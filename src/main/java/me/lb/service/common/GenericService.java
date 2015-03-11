package me.lb.service.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import me.lb.model.pagination.Pagination;

public interface GenericService<T, PK extends Serializable> {

	public T findById(PK id);

	public List<T> findAll();

	public List<T> findAll(Map<String, Object> params);

	public PK save(T entity);

	public void update(T entity);

	public void delete(T entity);

	public void deleteAll();

	public void deleteAll(Collection<T> entities);

	public Pagination<T> pagingQuery();

	public Pagination<T> pagingQuery(Map<String, Object> params);

}