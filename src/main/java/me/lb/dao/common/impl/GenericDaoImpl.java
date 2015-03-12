package me.lb.dao.common.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lb.dao.common.GenericDao;
import me.lb.model.pagination.Pagination;
import me.lb.support.system.SystemContext;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("unchecked")
public abstract class GenericDaoImpl<T, PK extends Serializable> implements
		GenericDao<T, PK> {

	@Autowired
	protected SessionFactory sessionFactory;
	protected Class<T> clazz;

	public GenericDaoImpl() {
		clazz = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public T findById(PK id) {
		return (T) sessionFactory.getCurrentSession().get(clazz, id);
	}

	@Override
	public List<T> findAll() {
		Query q = sessionFactory.getCurrentSession().createQuery(
				"from " + clazz.getName());
		return q.list();
	}

	@Override
	public List<T> findAll(Map<String, Object> params) {
		if (params.entrySet().size() > 0) {
			StringBuffer sb = new StringBuffer("from " + clazz.getName()
					+ " as o where 1=1");
			List<Object> objs = new ArrayList<Object>();
			Iterator<Map.Entry<String, Object>> it = params.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> me = it.next();
				sb.append(" and o." + me.getKey() + " = ?");
				objs.add(me.getValue());
			}
			Query q = sessionFactory.getCurrentSession().createQuery(
					sb.toString());
			for (int i = 0; i < objs.size(); i++) {
				q.setParameter(i, objs.get(i));
			}
			return q.list();
		} else {
			return new ArrayList<T>();
		}
	}

	@Override
	public PK save(T entity) {
		return (PK) sessionFactory.getCurrentSession().save(entity);
	}

	@Override
	public void update(T entity) {
		sessionFactory.getCurrentSession().update(entity);
	}

	@Override
	public void delete(T entity) {
		sessionFactory.getCurrentSession().delete(entity);
	}

	@Override
	public void deleteAll() {
		List<T> temp = this.findAll();
		deleteAll(temp);
	}

	@Override
	public void deleteAll(Collection<T> entities) {
		Session s = sessionFactory.getCurrentSession();
		Iterator<T> it = entities.iterator();
		while (it.hasNext()) {
			s.delete(it.next());
		}
	}

	protected Pagination<T> getPagination(final String hql,
			final List<Object> params) {
		List<T> datas = null;
		int count = 0;
		int index = hql.indexOf("from");
		if (index != -1) {
			count = ((Long) objectQuery(
					"select count(*) " + hql.substring(index), params))
					.intValue();
			Query query = sessionFactory.getCurrentSession().createQuery(hql);
			if (params != null) {
				for (int i = 0; i < params.size(); i++) {
					query.setParameter(i, params.get(i));
				}
			}
			query.setMaxResults(SystemContext.getPageSize());
			query.setFirstResult(SystemContext.getOffset());
			datas = query.list();
		} else {
			datas = new ArrayList<T>();
		}
		return new Pagination<T>(count, datas);
	}

	private Object objectQuery(final String hql, final List<Object> params) {
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				query.setParameter(i, params.get(i));
			}
		}
		return query.uniqueResult();
	}

}
