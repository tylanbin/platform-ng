package me.lb.controller.admin.system;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import me.lb.model.pagination.Pagination;
import me.lb.model.system.Emp;
import me.lb.service.system.EmpService;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/admin/system/emp")
public class EmpController {

	@Autowired
	private EmpService empService;

	@InitBinder
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		// 对Date类型参数传递的处理
		// DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		CustomDateEditor dateEditor = new CustomDateEditor(format, true);
		binder.registerCustomEditor(Date.class, dateEditor);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String listPage() {
		return "admin/system/emp/list";
	}

	@ResponseBody
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(Emp obj) {
		try {
			empService.save(obj);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			return "{ \"msg\" : \"添加失败，请确认操作无误后重试，或联系管理员处理！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public String edit(@PathVariable int id, Emp temp) {
		try {
			Emp obj = empService.findById(id);
			obj.setOrg(temp.getOrg());
			obj.setName(temp.getName());
			obj.setGender(temp.getGender());
			obj.setJob(temp.getJob());
			obj.setEducation(temp.getEducation());
			obj.setBirthday(temp.getBirthday());
			obj.setContact(temp.getContact());
			obj.setIdNumber(temp.getIdNumber());
			obj.setEmail(temp.getEmail());
			obj.setIsOnJob(temp.getIsOnJob());
			obj.setDateOfEntry(temp.getDateOfEntry());
			obj.setDateOfConfirm(temp.getDateOfConfirm());
			obj.setDateOfLeave(temp.getDateOfLeave());
			empService.update(obj);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			return "{ \"msg\" : \"修改失败，请确认操作无误后重试，或联系管理员处理！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable int id) {
		try {
			empService.delete(empService.findById(id));
			return "{ \"success\" : true }";
		} catch (Exception e) {
			return "{ \"msg\" : \"删除失败，请确认操作无误后重试，或联系管理员处理！\" }";
		}
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
		Pagination<Emp> pm = null;
		if (params == null) {
			pm = empService.pagingQuery();
		} else {
			pm = empService.pagingQuery(params);
		}
		// 序列化查询结果为JSON
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", pm.getTotal());
		map.put("rows", pm.getDatas());
		return om.writeValueAsString(map);
	}

}
