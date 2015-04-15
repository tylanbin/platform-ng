package me.lb.support.jackson;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;

/**
 * jackson的属性过滤工具
 * 该实体需要加入注解@JsonFilter("unknown")，以使用默认属性过滤器
 * @author lanbin
 * @date 2015-4-15
 */
public class JsonWriter {

	public static ObjectWriter except(Class<?> clazz, String... arr) {
		ObjectMapper om = new ObjectMapper();
		SimpleFilterProvider sfp = new SimpleFilterProvider();
		sfp.addFilter(clazz.getSimpleName(),
				SimpleBeanPropertyFilter.serializeAllExcept(arr));
		return om.writer(sfp);
	}

}
