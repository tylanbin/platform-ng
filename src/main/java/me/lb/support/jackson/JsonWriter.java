package me.lb.support.jackson;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;

/**
 * jackson的属性过滤工具
 * 该实体需要加入注解@JsonFilter
 * @author lanbin
 * @date 2015-4-15
 */
public class JsonWriter {

	private SimpleFilterProvider sfp;

	private JsonWriter() {
		this.sfp = new SimpleFilterProvider();
	}

	/**
	 * 获取实例
	 */
	public static JsonWriter getInstance() {
		return new JsonWriter();
	}

	/**
	 * 过滤某类的某属性
	 * @param clazz 类
	 * @param arr 属性数组
	 */
	public JsonWriter filter(Class<?> clazz, String... arr) {
		this.sfp.addFilter(clazz.getSimpleName(),
				SimpleBeanPropertyFilter.serializeAllExcept(arr));
		return this;
	}

	/**
	 * 获取ObjectWriter，输出json
	 */
	public ObjectWriter getWriter() {
		ObjectMapper om = new ObjectMapper();
		return om.writer(this.sfp);
	}

}
