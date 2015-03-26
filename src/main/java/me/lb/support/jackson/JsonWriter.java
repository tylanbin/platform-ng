package me.lb.support.jackson;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;

public class JsonWriter {

	public static ObjectWriter getWriter(String... arr) {
		ObjectMapper om = new ObjectMapper();
		SimpleFilterProvider sfp = new SimpleFilterProvider();
		sfp.setDefaultFilter(SimpleBeanPropertyFilter.serializeAllExcept(arr));
		return om.writer(sfp);
	}

}
