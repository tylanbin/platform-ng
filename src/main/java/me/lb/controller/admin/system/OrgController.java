package me.lb.controller.admin.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lb.model.pagination.Pagination;
import me.lb.model.system.Org;
import me.lb.service.system.OrgService;

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
@RequestMapping(value = "/admin/system/org")
public class OrgController {

	@Autowired
	private OrgService orgService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list() {
		return "admin/system/org/list";
	}

	@ResponseBody
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(Org obj) {
		try {
			orgService.save(obj);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			return "{ \"msg\" : \"添加失败，请确认操作无误后重试，或联系管理员处理！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public String edit(@PathVariable int id, Org temp) {
		try {
			Org obj = orgService.findById(id);
			obj.setOrg(temp.getOrg());
			obj.setName(temp.getName());
			obj.setSerialNum(temp.getSerialNum());
			obj.setWorkPlace(temp.getWorkPlace());
			obj.setContact(temp.getContact());
			obj.setLeader(temp.getLeader());
			orgService.update(obj);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			return "{ \"msg\" : \"修改失败，请确认操作无误后重试，或联系管理员处理！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable int id) {
		try {
			orgService.delete(orgService.findById(id));
			return "{ \"success\" : true }";
		} catch (Exception e) {
			return "{ \"msg\" : \"删除失败，请确认操作无误后重试，或联系管理员处理！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/tree", method = RequestMethod.GET)
	public String tree() throws Exception {
		List<Org> list = orgService.findTopOrgs();
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
			// Perm param = om.readValue(jsonParam, Perm.class);
			params = om.readValue(jsonParam,
					new TypeReference<Map<String, Object>>() {
					});
		}
		// 传参查询
		Pagination<Org> pm = null;
		if (params == null) {
			pm = orgService.pagingQuery();
		} else {
			pm = orgService.pagingQuery(params);
		}
		// 序列化查询结果为JSON
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", pm.getTotal());
		map.put("rows", pm.getDatas());
		return om.writeValueAsString(map);
	}

}
