package me.lb.controller.admin.process;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import me.lb.model.system.User;
import me.lb.service.system.UserService;
import me.lb.support.system.SystemContext;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.NativeHistoricProcessInstanceQuery;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@RequestMapping(value = "/admin/process/ins")
public class InsController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private ProcessEngineFactoryBean processEngineFactory;
	
	/**
	 * 读取流程实例的运行状态图
	 * @param piId 流程实例id
	 */
	@RequestMapping(value = "/{piId}/resource/img", method = RequestMethod.GET)
	public void resource(@PathVariable String piId, HttpServletResponse response) {
		try {
			ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(piId).singleResult();
			BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
			// 取得当前正在执行中的节点
			List<String> currentIds = runtimeService.getActiveActivityIds(piId);
			ProcessEngineConfigurationImpl config = processEngineFactory.getProcessEngineConfiguration();
			Context.setProcessEngineConfiguration(config);
			// 生成图片
			InputStream input = config.getProcessDiagramGenerator()
					.generateDiagram(bpmnModel, "png", currentIds, Collections.<String>emptyList(), "黑体", "黑体", null, 1.0);
			IOUtils.copy(input, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 批量删除流程实例
	 * @param ids 流程实例id的集合
	 */
	@ResponseBody
	@RequestMapping(value = "/batch", method = RequestMethod.DELETE)
	public String batch_delete(String ids, String reason) {
		try {
			String[] temp = ids.split(",");
			for (String id : temp) {
				runtimeService.deleteProcessInstance(id, reason);
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}
	
	/**
	 * 挂起/激活流程实例
	 * @param piId 流程实例id
	 * @param type 操作类型：active/suspend
	 */
	@ResponseBody
	@RequestMapping(value = "/{piId}/state/{type}", method = RequestMethod.PUT)
	public String state(@PathVariable String piId, @PathVariable String type) {
		try {
			if ("active".equals(type)) {
				runtimeService.activateProcessInstanceById(piId);
			} else if ("suspend".equals(type)) {
				runtimeService.suspendProcessInstanceById(piId);
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}
	
	/**
	 * 查看流程实例的历史数据
	 * @param piId 流程实例id
	 */
	@ResponseBody
	@RequestMapping(value = "/{piId}/tasks", method = RequestMethod.GET)
	public String hisTasks(@PathVariable String piId) {
		try {
			ObjectMapper om = new ObjectMapper();
			List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
					.processInstanceId(piId)
					.orderByTaskId().asc()
					.list();
			return om.writeValueAsString(detailHistoricTaskInstance(list));
		} catch (Exception e) {
			return "[]";
		}
	}
	
	/**
	 * 查看流程实例的历史数据
	 * @param piId 流程实例id
	 */
	@ResponseBody
	@RequestMapping(value = "/{piId}/datas", method = RequestMethod.GET)
	public String hisDatas(@PathVariable String piId) {
		try {
			ObjectMapper om = new ObjectMapper();
			List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery()
					.processInstanceId(piId)
					.list();
			return om.writeValueAsString(list);
		} catch (Exception e) {
			return "[]";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/data/{type}", method = RequestMethod.GET)
    public String data(@PathVariable String type, String params) {
		try {
			// 处理查询参数
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> map = new HashMap<String, Object>();
			if (!StringUtils.isEmpty(params)) {
				map = om.readValue(params, new TypeReference<Map<String, Object>>() {});
			}
			// 查询执行结束的流程实例
			StringBuffer sql = new StringBuffer("from ACT_HI_PROCINST RES inner join ACT_RE_PROCDEF DEF on RES.PROC_DEF_ID_ = DEF.ID_");
			// 只查询结束的流程
			if ("running".equals(type)) {
				sql.append(" where RES.END_TIME_ is NULL");
			} else if ("his".equals(type)) {
				sql.append(" where RES.END_TIME_ is not NULL");
			} else {
				sql.append(" where 1 = 1");
			}
			// 追加sql
			if (map.containsKey("pdKeyLike")) {
				sql.append(" and DEF.KEY_ = #{pdKeyLike}");
			}
			if (map.containsKey("pdNameLike")) {
				sql.append(" and DEF.NAME_ like #{pdNameLike}");
			}
			if (map.containsKey("piNameLike")) {
				sql.append(" and RES.NAME_ like #{piNameLike}");
			}
			if (map.containsKey("startedAfter")) {
				sql.append(" and RES.START_TIME_ >= #{startedAfter}");
			}
			if (map.containsKey("finishedBefore")) {
				sql.append(" and RES.END_TIME_ <= #{finishedBefore}");
			}
			NativeHistoricProcessInstanceQuery q = historyService.createNativeHistoricProcessInstanceQuery();
			// 先查询数据
			q.sql("select RES.* " + sql);
			// 处理参数
			if (map.containsKey("pdKeyLike")) {
				q.parameter("pdKeyLike", "%" + map.get("pdKeyLike") + "%");
			}
			if (map.containsKey("pdNameLike")) {
				q.parameter("pdNameLike", "%" + map.get("pdNameLike") + "%");
			}
			if (map.containsKey("piNameLike")) {
				q.parameter("piNameLike", "%" + map.get("piNameLike") + "%");
			}
			if (map.containsKey("startedAfter")) {
				q.parameter("startedAfter", map.get("startedAfter"));
			}
			if (map.containsKey("finishedBefore")) {
				q.parameter("finishedBefore", map.get("finishedBefore"));
			}
			List<HistoricProcessInstance> rows = q.listPage(SystemContext.getOffset(), SystemContext.getPageSize());
			// 再查询总数
			long total = q.sql("select count(distinct RES.ID_) " + sql).count();
			// 序列化查询结果为JSON
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", total);
			result.put("rows", detailHistoricProcessInstance(rows));
			return om.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
    }
	
	// 私有方法
	
	/**
	 * 将历史流程实例的信息详细化
	 * @param hpis 历史流程实例集合
	 */
	private ArrayNode detailHistoricProcessInstance(List<HistoricProcessInstance> hpis) throws Exception {
		ObjectMapper om = new ObjectMapper();
		ArrayNode arr = om.createArrayNode();
		for (HistoricProcessInstance hpi : hpis) {
			ObjectNode on = (ObjectNode) om.readTree(om.writeValueAsString(hpi));
			ProcessDefinition pd = repositoryService.getProcessDefinition(hpi.getProcessDefinitionId());
			on.put("processDefinitionKey", pd.getKey());
			on.put("processDefinitionName", pd.getName());
			User user = userService.findById(Integer.valueOf(hpi.getStartUserId()));
			on.put("startUser", user.getEmp().getName());
			arr.add(on);
		}
		return arr;
	}
	
	/**
	 * 将历史任务的信息详细化
	 * @param htis 历史任务集合
	 */
	private ArrayNode detailHistoricTaskInstance(List<HistoricTaskInstance> htis) throws Exception {
		ObjectMapper om = new ObjectMapper();
		ArrayNode arr = om.createArrayNode();
		for (HistoricTaskInstance hti : htis) {
			ObjectNode on = (ObjectNode) om.readTree(om.writeValueAsString(hti));
			if (!StringUtils.isEmpty(hti.getAssignee())) {
				User user = userService.findById(Integer.valueOf(hti.getAssignee()));
				on.put("assigneeName", user.getEmp().getName());
			} else {
				on.put("assigneeName", "");
			}
			arr.add(on);
		}
		return arr;
	}

}
