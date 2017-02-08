package me.lb.controller.admin.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lb.model.pagination.Pagination;
import me.lb.model.system.Emp;
import me.lb.model.system.Org;
import me.lb.service.system.EmpService;
import me.lb.service.system.OrgService;
import me.lb.support.jackson.JsonWriter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(value = "/admin/system/emp")
public class EmpController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EmpService empService;
	@Autowired
	private OrgService orgService;

	@InitBinder
	protected void initBinder(ServletRequestDataBinder binder) {
		// 对Date类型参数传递的处理
		// DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		CustomDateEditor dateEditor = new CustomDateEditor(format, true);
		binder.registerCustomEditor(Date.class, dateEditor);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String listPage() {
		logger.info("invoke" + getClass().getName() + "." + "list");
		return "admin/system/emp/list";
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String edit(@PathVariable int id, Emp temp, Integer orgId) {
		try {
			Emp obj = empService.findById(id);
			obj.setName(temp.getName());
			obj.setGender(temp.getGender());
			obj.setJob(temp.getJob());
			obj.setEducation(temp.getEducation());
			obj.setBirthday(temp.getBirthday());
			obj.setContact(temp.getContact());
			obj.setIdCard(temp.getIdCard());
			obj.setEmail(temp.getEmail());
			// obj.setIsOnJob(temp.getIsOnJob());
			// obj.setDateOfEntry(temp.getDateOfEntry());
			// obj.setDateOfConfirm(temp.getDateOfConfirm());
			// obj.setDateOfLeave(temp.getDateOfLeave());
			if (orgId != null) {
				// 修改所属机构
				Org org = orgService.findById(orgId);
				obj.setOrg(org);
			}
			empService.update(obj);
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
			empService.delete(empService.findById(id));
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
			Emp temp = empService.findById(id);
			// 将查询出的结果序列化为JSON并返回
			return JsonWriter.getInstance()
					.filter(Emp.class, "users")
					.filter(Org.class, "org", "emps", "roles", "orgs", "children")
					.getWriter().writeValueAsString(temp);
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/batch", method = RequestMethod.POST)
	public String batch_add(Org org, String objs) {
		try {
			ObjectMapper om = new ObjectMapper();
			List<Emp> list = om.readValue(objs, new TypeReference<List<Emp>>() {});
			Iterator<Emp> it = list.iterator();
			while (it.hasNext()) {
				Emp obj = it.next();
				if (org.getId() != null) {
					obj.setOrg(org);
				}
				empService.save(obj);
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
				Emp emp = empService.findById(Integer.parseInt(id.trim()));
				empService.delete(emp);
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
		try {
			ObjectMapper om = new ObjectMapper();
			Pagination<Emp> pm = null;
			if (!StringUtils.isEmpty(params)) {
				Map<String, Object> map = om.readValue(params, new TypeReference<Map<String, Object>>() {});
				pm = empService.pagingQuery(map);
			} else {
				pm = empService.pagingQuery();
			}
			// 序列化查询结果为JSON
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", pm.getTotal());
			result.put("rows", pm.getDatas());
			return JsonWriter.getInstance()
					.filter(Emp.class, "users")
					.filter(Org.class, "org", "emps", "roles", "orgs", "children")
					.getWriter().writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
	}

}
