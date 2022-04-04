package me.lb.dao.common.impl;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import me.lb.dao.common.GenericDao;
import me.lb.model.pagination.Pagination;
import me.lb.support.jackson.JsonWriter;
import me.lb.support.system.SystemContext;

public abstract class GenericDaoImpl<T> implements GenericDao<T> {
	
	@Autowired
	protected SqlSessionTemplate sqlSessionTemplate;
	
	// 记录使用的通用的SQL配置文件
	private static final String PKG = "me.lb.model.common.";
	private Class<T> clazz;
	
	@SuppressWarnings("unchecked")
	public GenericDaoImpl() {
		clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	// 记录使用的表名称
	protected abstract String getTableName();
	// 记录新增修改时不处理的字段
	protected abstract String[] getIgnored();

	@Override
	public T findById(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		// 构造参数
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", getTableName());
		map.put("params", params);
		Map<String, Object> data = sqlSessionTemplate.selectOne(PKG + "find", map);
		return convertMapToPojo(data);
	}

	@Override
	public List<T> findAll() {
		// 构造参数
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", getTableName());
		List<Object> datas = sqlSessionTemplate.selectList(PKG + "find", map);
		return convertMapToPojo(datas);
	}

	@Override
	public List<T> findAll(Map<String, Object> params) {
		// 处理参数（区分是否是like）
		Map<String, Object> likeParams = handleParams(params);
		// 构造参数
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", getTableName());
		map.put("params", params);
		map.put("likeParams", likeParams);
		List<Object> datas = sqlSessionTemplate.selectList(PKG + "find", map);
		return convertMapToPojo(datas);
	}

	@Override
	public int save(T obj) {
		// 将对象转化为map
		Map<String, Object> datas = convertPojoToMap(obj);
		// 拆分map为列与值的对应
		List<String> cols = new ArrayList<String>();
		List<Object> vals = new ArrayList<Object>();
		Iterator<Map.Entry<String, Object>> it = datas.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> me = it.next();
			cols.add(me.getKey());
			vals.add(me.getValue());
		}
		// 构造参数
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", getTableName());
		map.put("cols", cols);
		map.put("vals", vals);
		sqlSessionTemplate.insert(PKG + "save", map);
		return Integer.valueOf(String.valueOf(map.get("id")));
	}

	@Override
	public void update(int id, T obj) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		// 将对象转化为map
		Map<String, Object> datas = convertPojoToMap(obj);
		// 构造参数
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", getTableName());
		map.put("datas", datas);
		map.put("params", params);
		sqlSessionTemplate.update(PKG + "update", map);
	}

	@Override
	public void delete(int id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		// 构造参数
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", getTableName());
		map.put("params", params);
		sqlSessionTemplate.delete(PKG + "delete", map);
	}

	@Override
	public void deleteAll() {
		// 构造参数
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", getTableName());
		map.put("col", "id");
		sqlSessionTemplate.delete(PKG + "deleteAll", map);
	}

	@Override
	public void deleteAll(List<Integer> ids) {
		// 构造参数
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", getTableName());
		map.put("col", "id");
		map.put("ids", ids);
		sqlSessionTemplate.delete(PKG + "deleteAll", map);
	}

	@Override
	public Pagination<T> pagingQuery() {
		// 先查询条数
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", getTableName());
		int total = sqlSessionTemplate.selectOne(PKG + "pagingCount", map);
		// 再分页查询
		map.put("offset", SystemContext.getOffset());
		map.put("limit", SystemContext.getPageSize());
		List<Object> datas = sqlSessionTemplate.selectList(PKG + "pagingFind", map);
		return new Pagination<T>(total, convertMapToPojo(datas));
	}

	@Override
	public Pagination<T> pagingQuery(Map<String, Object> params) {
		// 处理参数（区分是否是like）
		Map<String, Object> likeParams = handleParams(params);
		// 先查询条数
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", getTableName());
		map.put("params", params);
		map.put("likeParams", likeParams);
		int total = sqlSessionTemplate.selectOne(PKG + "pagingCount", map);
		// 再分页查询
		map.put("offset", SystemContext.getOffset());
		map.put("limit", SystemContext.getPageSize());
		List<Object> datas = sqlSessionTemplate.selectList(PKG + "pagingFind", map);
		return new Pagination<T>(total, convertMapToPojo(datas));
	}

	/**
	 * 将实体对象转换为map
	 * @param obj 实体对象
	 * @return map
	 */
	private Map<String, Object> convertPojoToMap(Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ObjectMapper om = new ObjectMapper();
			// 处理过滤掉的属性
			String json = JsonWriter.getInstance()
					.filter(clazz, getIgnored()).getWriter()
					.writeValueAsString(obj);
			map = om.readValue(json, new TypeReference<Map<String, Object>>() {});
			// 默认id不处理
			map.remove("id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 将map转换为实体对象
	 * @param map map
	 * @return 实体对象
	 */
	private T convertMapToPojo(Map<String, Object> map) {
		T obj = null;
		try {
			ObjectMapper om = new ObjectMapper();
			String json = om.writeValueAsString(map);
			obj = om.readValue(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	/**
	 * 将map集合转换为实体对象集合
	 * @param list map集合
	 * @return 实体对象集合
	 */
	private List<T> convertMapToPojo(List<Object> list) {
		List<T> objs = new ArrayList<T>();
		try {
			ObjectMapper om = new ObjectMapper();
			om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			om.registerModule(new JavaTimeModule());
			String json = om.writeValueAsString(list);
			objs = om.readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objs;
	}
	
	/**
	 * 处理查询的参数，其中有可能有的参数以Like结尾，将其提出
	 * @param params 原始的参数map
	 * @return 使用like方式查询的参数map
	 */
	private Map<String, Object> handleParams(Map<String, Object> params) {
		Map<String, Object> likeParams = new HashMap<String, Object>();
		Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> me = it.next();
			if (me.getKey().endsWith("Like")) {
				// 以Like结尾，将其从params中提取出
				String key = me.getKey().substring(0, me.getKey().length() - 4);
				likeParams.put(key, me.getValue());
				it.remove();
			} else {
				// 不是则不需要处理
			}
		}
		return likeParams;
	}

}
