package me.lb.controller.admin.system;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lb.model.pagination.Pagination;
import me.lb.model.system.Perm;
import me.lb.service.system.PermService;
import me.lb.support.jackson.JsonWriter;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/admin/system/perm")
public class PermController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private PermService permService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String listPage() {
		logger.info("invoke" + getClass().getName() + "." + "list");
		return "admin/system/perm/list";
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String edit(@PathVariable int id, Perm temp, Integer pid) {
		try {
			Perm obj = permService.findById(id);
			obj.setName(temp.getName());
			obj.setToken(temp.getToken());
			obj.setUrl(temp.getUrl());
			if (pid != null) {
				// 修改父对象
				Perm par = permService.findById(pid);
				obj.setPerm(par);
			}
			permService.update(obj);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable int id) {
		try {
			permService.delete(permService.findById(id));
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String get(@PathVariable int id) {
		try {
			Perm temp = permService.findById(id);
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter.getInstance()
					.filter(Perm.class, "roles", "perms", "children")
					.getWriter().writeValueAsString(temp);
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/batch", method = RequestMethod.POST)
	public String batch_add(Perm par, String objs) {
		ObjectMapper om = new ObjectMapper();
		try {
			List<Perm> list = om.readValue(objs,
					new TypeReference<List<Perm>>() {
					});
			Iterator<Perm> it = list.iterator();
			while (it.hasNext()) {
				Perm obj = it.next();
				if (par.getId() != null) {
					obj.setPerm(par);
				}
				permService.save(obj);
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
		try {
			String[] temp = ids.split(",");
			for (String id : temp) {
				permService.delete(permService.findById(Integer.parseInt(id)));
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/tree", method = RequestMethod.GET)
	public String tree() {
		try {
			List<Perm> list = permService.findTopPerms();
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter.getInstance()
					.filter(Perm.class, "perm", "roles", "perms").getWriter()
					.writeValueAsString(list);
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/data", method = RequestMethod.GET)
	public String data(String params) {
		ObjectMapper om = new ObjectMapper();
		try {
			Pagination<Perm> pm = null;
			if (!StringUtils.isEmpty(params)) {
				// Perm vo = om.readValue(jsonParam, Perm.class);
				Map<String, Object> map = om.readValue(params,
						new TypeReference<Map<String, Object>>() {
						});
				pm = permService.pagingQuery(map);
			} else {
				pm = permService.pagingQuery();
			}
			// 序列化查询结果为JSON
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", pm.getTotal());
			result.put("rows", pm.getDatas());
			return JsonWriter.getInstance()
					.filter(Perm.class, "roles", "perms", "children")
					.getWriter().writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
	}

}
