package me.lb.controller.admin.process.handler.formkey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.lb.model.system.User;
import me.lb.support.system.SystemContext;
import me.lb.utils.ActivitiUtil;
import me.lb.utils.UserUtil;

import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.NativeProcessDefinitionQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 外置表单（formkey）类型流程的处理
 * @author lanbin
 * @date 2017-2-3
 */
@Controller
@RequestMapping(value = "/admin/process/formkey")
public class FormkeyController {
	
	@Autowired
	private FormService formService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private RepositoryService repositoryService;

	/**
	 * 可发起的流程定义
	 */
	@ResponseBody
	@RequestMapping(value = "/process/startList", method = RequestMethod.GET)
	public String startList(String params, HttpSession session) {
		try {
			User user = UserUtil.getUserFromSession(session);
			// 处理查询参数
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> map = new HashMap<String, Object>();
			if (!StringUtils.isEmpty(params)) {
				map = om.readValue(params, new TypeReference<Map<String, Object>>() {});
			}
			// 由于封装的查询不支持latestVersion与nameLike并列，所以这里改为自定义查询
			// 编写的sql需要参考activiti-engine-x.x.x.jar中的org.activiti.db.mapping包
			StringBuffer sql = new StringBuffer("from ACT_RE_PROCDEF RES where 1=1");
			// 最新的版本
			sql.append(" and RES.VERSION_ = (select max(VERSION_) from ACT_RE_PROCDEF where KEY_ = RES.KEY_)");
			// 可以由用户发起
			sql.append(" and (");
			sql.append(" exists (select ID_  from ACT_RU_IDENTITYLINK IDN where IDN.PROC_DEF_ID_ = RES.ID_ and IDN.USER_ID_ = #{userId})");
			sql.append(" or exists (select ID_ from ACT_RU_IDENTITYLINK IDN where IDN.PROC_DEF_ID_ = RES.ID_ and IDN.GROUP_ID_ IN (select MS.GROUP_ID_ from ACT_ID_MEMBERSHIP MS where MS.USER_ID_ = #{userId}))");
			sql.append(")");
			// 级联查询参数
			if (map.containsKey("pdKeyLike")) {
				sql.append(" and RES.KEY_ like #{pdKeyLike}");
			}
			if (map.containsKey("pdNameLike")) {
				sql.append(" and RES.NAME_ like #{pdNameLike}");
			}
			if (map.containsKey("pdCategoryLike")) {
				sql.append(" and RES.CATEGORY_ like #{pdCategoryLike}");
			}
			// 按照key顺序排序
			sql.append(" order by RES.KEY_ asc");
			// 创建自定义查询
			NativeProcessDefinitionQuery q = repositoryService.createNativeProcessDefinitionQuery();
			// 先查询数据
			q.sql("select RES.* " + sql);
			q.parameter("userId", user.getId());
			if (map.containsKey("pdKeyLike")) {
				q.parameter("pdKeyLike", "%" + map.get("pdKeyLike") + "%");
			}
			if (map.containsKey("pdNameLike")) {
				q.parameter("pdNameLike", "%" + map.get("pdNameLike") + "%");
			}
			if (map.containsKey("pdCategoryLike")) {
				q.parameter("pdCategoryLike", "%" + map.get("pdCategoryLike") + "%");
			}
			List<ProcessDefinition> list = q.listPage(SystemContext.getOffset(), SystemContext.getPageSize());
			// 再查询总数
			long total = q.sql("select count(RES.ID_) " + sql).count();
			// 直接使用该list会出现异常（Direct self-reference leading to cycle），所以需要进行处理
			List<Object> datas = new ArrayList<Object>();
			for (ProcessDefinition pd : list) {
				datas.add(ActivitiUtil.convertProcessDefinitionToMap(pd));
			}
			// 序列化查询结果为JSON
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", total);
			result.put("rows", datas);
			// 不是自己的实体类，不需要进行输出过滤
			return om.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
	}
	
	/**
	 * 获取开始节点设置的form
	 * @param pdId 流程定义id
	 */
	@ResponseBody
	@RequestMapping(value = "/process/{pdId}/form", method = RequestMethod.GET)
	public Object getStartFormKey(@PathVariable String pdId) {
		try {
			// 在部署流程时，将设计的表单一同压缩在zip中进行部署，这里就可以直接获取表单内容了
			Object form = formService.getRenderedStartForm(pdId);
			return form;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 发起流程实例
	 * @param pdId 流程定义id
	 * @param request 请求中包括了表单的参数
	 */
	@ResponseBody
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/process/{pdId}/start", method = RequestMethod.POST)
	public String startProcess(@PathVariable String pdId, String piName, HttpServletRequest request) {
		try {
			// 获取用户登录信息
			User user = UserUtil.getUserFromSession(request.getSession());
			if (user != null) {
				// 用户登录才可以发起流程
				// 使用builder可以设置流程实例的属性
				ProcessInstanceBuilder builder = runtimeService.createProcessInstanceBuilder();
				// 从request中读取参数然后转换
				Map<String, String[]> map = request.getParameterMap();
				Iterator<Map.Entry<String, String[]>> it = map.entrySet().iterator();
				// 除特殊字段外都需要存储
				while (it.hasNext()) {
					Map.Entry<String, String[]> me = it.next();
					String name = me.getKey();
					if (!"piName".equals(name)) {
						// 这样处理可以兼容复选框（,分隔）
						String value = Arrays.toString(me.getValue());
						value = value.substring(1, value.length() - 1);
						// 记录到builder中
						builder.addVariable(name, value);
					}
				}
				// 设置发起人
				identityService.setAuthenticatedUserId(String.valueOf(user.getId()));
				// TODO: 这里预留了流程实例的名称，开始先直接使用流程定义名称即可
				builder.processDefinitionId(pdId);
				builder.processInstanceName(piName);
				ProcessInstance pi = builder.start();
				return "{ \"success\" : true, \"piId\" : \"" + pi.getId() + "\" }";
			} else {
				return "{ \"msg\" : \"未登录用户禁止发起流程！\" }";
			}
		} catch (Exception e) {
			e.printStackTrace();
			identityService.setAuthenticatedUserId(null);
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}
	
	/**
	 * 待办任务
	 */
	@ResponseBody
	@RequestMapping(value = "/task/todoList", method = RequestMethod.GET)
	public String todoList(String params, HttpSession session) {
		try {
			User user = UserUtil.getUserFromSession(session);
	        ObjectMapper om = new ObjectMapper();
			Map<String, Object> map = new HashMap<String, Object>();
			if (!StringUtils.isEmpty(params)) {
				map = om.readValue(params, new TypeReference<Map<String, Object>>() {});
			}
			TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(String.valueOf(user.getId())).active();
			// 级联查询参数
			if (map.containsKey("pdKeyLike")) {
				query = query.processDefinitionKeyLike("%" + map.get("pdKeyLike") + "%");
			}
			if (map.containsKey("pdNameLike")) {
				query = query.processDefinitionNameLike("%" + map.get("pdNameLike") + "%");
			}
			if (map.containsKey("taskNameLike")) {
				query = query.taskNameLikeIgnoreCase("%" + map.get("taskNameLike") + "%");
			}
			// 查询结果排序
			query = query.orderByTaskCreateTime().desc();
			// 查询结果（分页查询）
			long total = query.count();
			List<Task> list = query.listPage(SystemContext.getOffset(), SystemContext.getPageSize());
			// 直接使用该list会出现异常（Direct self-reference leading to cycle），所以需要进行处理
			List<Object> datas = new ArrayList<Object>();
			for (Task t : list) {
				datas.add(ActivitiUtil.convertTaskToMap(t));
			}
			// 序列化查询结果为JSON
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", total);
			result.put("rows", datas);
			return om.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
	}
	
	/**
	 * 获取任务节点设置的form
	 * @param taskId 流程实例的任务节点id（实例id）
	 */
	@ResponseBody
	@RequestMapping(value = "/task/{taskId}/form", method = RequestMethod.GET)
	public Object getTaskFormKey(@PathVariable String taskId) {
		try {
			Object form = formService.getRenderedTaskForm(taskId);
			return form;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 签收任务
	 * @param taskId 流程实例的任务节点id（实例id）
	 */
	@ResponseBody
	@RequestMapping(value = "/task/{taskId}/claim", method = RequestMethod.POST)
	public String claimTask(@PathVariable String taskId, HttpSession session) {
		try {
			// 获取用户登录信息
			User user = UserUtil.getUserFromSession(session);
			if (user != null) {
				// 用户登录才可以签收
				taskService.claim(taskId, String.valueOf(user.getId()));
				return "{ \"success\" : true }";
			} else {
				return "{ \"msg\" : \"未登录用户禁止签收任务！\" }";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}
	
	/**
	 * 办理任务
	 * @param taskId 流程实例的任务节点id（实例id）
	 * @param request 请求中包括了表单的参数
	 */
	@ResponseBody
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/task/{taskId}/complete", method = RequestMethod.POST)
	public String completeTask(@PathVariable String taskId, HttpServletRequest request) {
		try {
			// 由于办理的任务会根据用户来进行查询，所以这里不进行用户的验证
			// 参数处理
			Map<String, String> params = new HashMap<String, String>();
			// 从request中读取参数然后转换
			Map<String, String[]> map = request.getParameterMap();
			Iterator<Map.Entry<String, String[]>> it = map.entrySet().iterator();
			// 除特殊字段外都存放到map中
			while (it.hasNext()) {
				Map.Entry<String, String[]> me = it.next();
				String name = me.getKey();
				// 这样处理可以兼容复选框（,分隔）
				String value = Arrays.toString(me.getValue());
				value = value.substring(1, value.length() - 1);
				params.put(name, value);
			}
			// 提交表单并完成任务
			formService.submitTaskFormData(taskId, params);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}
	
}
