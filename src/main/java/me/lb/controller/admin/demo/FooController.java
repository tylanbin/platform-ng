package me.lb.controller.admin.demo;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;

import me.lb.support.jackson.JsonWriter;
import me.lb.service.demo.FooService;
import me.lb.model.pagination.Pagination;
import me.lb.model.demo.Foo;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/admin/demo/foo")
public class FooController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FooService fooService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String listPage() {
		// 跳转到列表页面的方法
		logger.info("invoke" + getClass().getName() + "." + "list");
		return "admin/demo/foo/list";
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String edit(@PathVariable int id, Foo temp) {
		// 修改某个对象的方法
		try {
			Foo obj = fooService.findById(id);
			// TODO: 这里需要根据实际进行完善

			fooService.update(obj);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable int id) {
		// 删除某个对象的方法
		try {
			fooService.delete(fooService.findById(id));
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String get(@PathVariable int id) {
		// 查询某个对象的方法
		try {
			Foo temp = fooService.findById(id);
			// 将查询出的结果序列化为JSON并返回
			// 这里可以使用filter方法，过滤不需要序列化的属性
			// 如果没有需要过滤的话，仍需要调用filter，避免错误
			return JsonWriter.getInstance().filter(Foo.class).getWriter()
					.writeValueAsString(temp);
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/batch", method = RequestMethod.POST)
	public String batch_add(String objs) {
		// 批量插入的操作
		ObjectMapper om = new ObjectMapper();
		try {
			List<Foo> list = om.readValue(objs, new TypeReference<List<Foo>>() {
			});
			Iterator<Foo> it = list.iterator();
			while (it.hasNext()) {
				Foo obj = it.next();
				fooService.save(obj);
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/batch", method = RequestMethod.DELETE)
	public String batch_delete(String ids) {
		// 批量删除的操作
		try {
			String[] temp = ids.split(",");
			for (String id : temp) {
				Foo obj = fooService.findById(Integer.parseInt(id.trim()));
				fooService.delete(obj);
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/data", method = RequestMethod.GET)
	public String data(String params) {
		// 查询数据集合的方法
		ObjectMapper om = new ObjectMapper();
		try {
			Pagination<Foo> pm = null;
			if (!StringUtils.isEmpty(params)) {
				Map<String, Object> map = om.readValue(params,
						new TypeReference<Map<String, Object>>() {
						});
				pm = fooService.pagingQuery(map);
			} else {
				pm = fooService.pagingQuery();
			}
			// 序列化查询结果为JSON
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", pm.getTotal());
			result.put("rows", pm.getDatas());
			// 这里可以使用filter方法，过滤不需要序列化的属性
			// 如果没有需要过滤的话，仍需要调用filter，避免错误
			return JsonWriter.getInstance().filter(Foo.class).getWriter()
					.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
	}

}
