package me.lb.controller.admin.process.handler.formkey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.lb.model.system.User;
import me.lb.utils.UserUtil;

import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	private IdentityService identityService;

	/**
	 * 获取开始节点设置的form
	 * @param pdId 流程定义id
	 */
	@ResponseBody
	@RequestMapping(value = "/form/start", method = RequestMethod.GET)
	public Object getStartFormKey(String pdId) {
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
	 * 获取任务节点设置的form
	 * @param taskId 流程实例的任务节点id（实例id）
	 */
	@ResponseBody
	@RequestMapping(value = "/form/task", method = RequestMethod.GET)
	public Object getTaskFormKey(String taskId) {
		try {
			Object form = formService.getRenderedTaskForm(taskId);
			return form;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 可发起的流程定义
	 */
	@ResponseBody
	@RequestMapping(value = "/process/startList", method = RequestMethod.GET)
	public String startList(HttpSession session) {
		try {
			// TODO: 缺少可发起的流程定义的列表
			return "{ \"total\" : 0, \"rows\" : [] }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
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
	public String startProcess(@PathVariable String pdId, HttpServletRequest request) {
		try {
			// 获取用户登录信息
			User user = UserUtil.getUserFromSession(request.getSession());
			if (user != null) {
				// 用户登录才可以发起流程
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
				// 设置发起人
				identityService.setAuthenticatedUserId(String.valueOf(user.getId()));
				ProcessInstance pi = formService.submitStartFormData(pdId, params);
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
	public String todoList(HttpSession session) {
		try {
			// TODO: 缺少待办任务列表
			return "{ \"total\" : 0, \"rows\" : [] }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
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
