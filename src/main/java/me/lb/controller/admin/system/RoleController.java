package me.lb.controller.admin.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.lb.model.pagination.Pagination;
import me.lb.model.system.Org;
import me.lb.model.system.Perm;
import me.lb.model.system.Role;
import me.lb.service.system.OrgService;
import me.lb.service.system.RoleService;
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
@RequestMapping(value = "/admin/system/role")
public class RoleController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RoleService roleService;
	@Autowired
	private OrgService orgService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String listPage() {
		logger.info("invoke" + getClass().getName() + "." + "list");
		return "admin/system/role/list";
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String edit(@PathVariable int id, Role temp, Integer orgId) {
		try {
			Role obj = roleService.findById(id);
			obj.setName(temp.getName());
			obj.setDescription(temp.getDescription());
			if (orgId != null) {
				// 修改所属机构
				Org org = orgService.findById(orgId);
				obj.setOrg(org);
			}
			roleService.update(obj);
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
			roleService.delete(roleService.findById(id));
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
			Role temp = roleService.findById(id);
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter
					.getInstance()
					.filter(Role.class, "users", "perms")
					.filter(Org.class, "org", "emps", "roles", "orgs",
							"children").getWriter().writeValueAsString(temp);
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/batch", method = RequestMethod.POST)
	public String batch_add(Org org, String objs) {
		ObjectMapper om = new ObjectMapper();
		try {
			List<Role> list = om.readValue(objs,
					new TypeReference<List<Role>>() {
					});
			Iterator<Role> it = list.iterator();
			while (it.hasNext()) {
				Role obj = it.next();
				if (org.getId() != null) {
					obj.setOrg(org);
				}
				roleService.save(obj);
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
				Role role = roleService.findById(Integer.parseInt(id.trim()));
				roleService.delete(role);
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
		ObjectMapper om = new ObjectMapper();
		try {
			Pagination<Role> pm = null;
			if (!StringUtils.isEmpty(params)) {
				Map<String, Object> map = om.readValue(params,
						new TypeReference<Map<String, Object>>() {
						});
				pm = roleService.pagingQuery(map);
			} else {
				pm = roleService.pagingQuery();
			}
			// 序列化查询结果为JSON
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", pm.getTotal());
			result.put("rows", pm.getDatas());
			// FIXME: 这里存在json级联序列化的问题，需要处理
			return JsonWriter
					.getInstance()
					.filter(Role.class, "users", "perms")
					.filter(Org.class, "org", "emps", "roles", "orgs",
							"children").getWriter().writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
	}

	// 授权的方法
	@ResponseBody
	@RequestMapping(value = "/{id}/auth", method = RequestMethod.POST)
	public String auth_post(@PathVariable int id, String permIds) {
		try {
			List<Integer> list = new ArrayList<Integer>();
			String[] temp = permIds.split(",");
			for (String permId : temp) {
				list.add(Integer.parseInt(permId.trim()));
			}
			roleService.auth(id, list);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{id}/auth", method = RequestMethod.GET)
	public String auth_get(@PathVariable int id) {
		try {
			Role temp = roleService.findById(id);
			Set<Perm> perms = temp.getPerms();
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter.getInstance()
					.filter(Perm.class, "perm", "roles", "perms", "children")
					.getWriter().writeValueAsString(perms);
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		}
	}

}
