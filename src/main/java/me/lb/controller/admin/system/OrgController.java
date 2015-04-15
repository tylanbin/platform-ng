package me.lb.controller.admin.system;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lb.model.pagination.Pagination;
import me.lb.model.system.Org;
import me.lb.service.system.OrgService;
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
@RequestMapping(value = "/admin/system/org")
public class OrgController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrgService orgService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String listPage() {
		logger.info("invoke" + getClass().getName() + "." + "list");
		return "admin/system/org/list";
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String edit(@PathVariable int id, Org temp, Integer pid) {
		try {
			Org obj = orgService.findById(id);
			obj.setName(temp.getName());
			obj.setSerialNum(temp.getSerialNum());
			obj.setWorkPlace(temp.getWorkPlace());
			obj.setContact(temp.getContact());
			obj.setLeader(temp.getLeader());
			if (pid != null) {
				// 修改父对象
				Org par = orgService.findById(pid);
				obj.setOrg(par);
			}
			orgService.update(obj);
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
			orgService.delete(orgService.findById(id));
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
			Org temp = orgService.findById(id);
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter.except("emps", "roles", "orgs", "children")
					.writeValueAsString(temp);
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/batch", method = RequestMethod.POST)
	public String batch_add(Org par, String objs) {
		ObjectMapper om = new ObjectMapper();
		try {
			List<Org> list = om.readValue(objs, new TypeReference<List<Org>>() {
			});
			Iterator<Org> it = list.iterator();
			while (it.hasNext()) {
				Org obj = it.next();
				if (par.getId() != null) {
					obj.setOrg(par);
				}
				orgService.save(obj);
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
				orgService.delete(orgService.findById(Integer.parseInt(id)));
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
			List<Org> list = orgService.findTopOrgs();
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter.except("org", "emps", "roles", "orgs")
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
			Pagination<Org> pm = null;
			if (!StringUtils.isEmpty(params)) {
				Map<String, Object> map = om.readValue(params,
						new TypeReference<Map<String, Object>>() {
						});
				pm = orgService.pagingQuery(map);
			} else {
				pm = orgService.pagingQuery();
			}
			// 序列化查询结果为JSON
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", pm.getTotal());
			result.put("rows", pm.getDatas());
			return JsonWriter.except("emps", "roles", "orgs", "children")
					.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
	}

}
