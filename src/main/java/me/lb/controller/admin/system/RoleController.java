package me.lb.controller.admin.system;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lb.model.pagination.Pagination;
import me.lb.model.system.Role;
import me.lb.service.system.RoleService;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/admin/system/role")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String listPage() {
		return "admin/system/role/list";
	}

	@ResponseBody
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(String jsonObjs) {
		ObjectMapper om = new ObjectMapper();
		try {
			List<Role> list = om.readValue(jsonObjs,
					new TypeReference<List<Role>>() {
					});
			Iterator<Role> it = list.iterator();
			while (it.hasNext()) {
				roleService.save(it.next());
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			return "{ \"msg\" : \"添加失败，请确认操作无误后重试，或联系管理员处理！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public String edit(@PathVariable int id, Role temp) {
		try {
			Role obj = roleService.findById(id);
			obj.setRole(temp.getRole());
			obj.setOrg(temp.getOrg());
			obj.setName(temp.getName());
			obj.setDescription(temp.getDescription());
			roleService.update(obj);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			return "{ \"msg\" : \"修改失败，请确认操作无误后重试，或联系管理员处理！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable int id) {
		try {
			roleService.delete(roleService.findById(id));
			return "{ \"success\" : true }";
		} catch (Exception e) {
			return "{ \"msg\" : \"删除失败，请确认操作无误后重试，或联系管理员处理！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/tree", method = RequestMethod.GET)
	public String tree() throws Exception {
		List<Role> list = roleService.findTopRoles();
		// 将查询出的结果序列化为JSON并返回
		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(list);
	}

	@ResponseBody
	@RequestMapping(value = "/data", method = RequestMethod.GET)
	public String data(String jsonParam) throws Exception {
		ObjectMapper om = new ObjectMapper();
		Map<String, Object> params = null;
		if (!StringUtils.isEmpty(jsonParam)) {
			// Role param = om.readValue(jsonParam, Role.class);
			params = om.readValue(jsonParam,
					new TypeReference<Map<String, Object>>() {
					});
		}
		// 传参查询
		Pagination<Role> pm = null;
		if (params == null) {
			pm = roleService.pagingQuery();
		} else {
			pm = roleService.pagingQuery(params);
		}
		// 序列化查询结果为JSON
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", pm.getTotal());
		map.put("rows", pm.getDatas());
		return om.writeValueAsString(map);
	}

}
