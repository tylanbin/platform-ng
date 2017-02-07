package me.lb.controller.admin.process;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import me.lb.support.system.SystemContext;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.NativeHistoricProcessInstanceQuery;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.runtime.NativeProcessInstanceQuery;
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

@Controller
@RequestMapping(value = "/admin/process/ins")
public class InsController {
	
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
			List<String> activeActivityIds = runtimeService.getActiveActivityIds(piId);
			ProcessEngineConfigurationImpl config = processEngineFactory.getProcessEngineConfiguration();
			Context.setProcessEngineConfiguration(config);
			// 生成图片
			InputStream input = config.getProcessDiagramGenerator().generateDiagram(bpmnModel, "png", activeActivityIds);
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
	
	@ResponseBody
	@RequestMapping(value = "/data/{type}", method = RequestMethod.GET)
    public String running(@PathVariable String type, String params) {
		try {
			// 处理查询参数
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> map = new HashMap<String, Object>();
			if (!StringUtils.isEmpty(params)) {
				map = om.readValue(params, new TypeReference<Map<String, Object>>() {});
			}
			if ("running".equals(type)) {
				// 查询正在运行的流程
				// 创建自定义查询（封装的不能按照流程定义名称模糊查询）
				// 编写的sql需要参考activiti-engine-x.x.x.jar中的org.activiti.db.mapping包
				StringBuffer sql = new StringBuffer("from ACT_RU_EXECUTION RES inner join ACT_RE_PROCDEF P on RES.PROC_DEF_ID_ = P.ID_");
				sql.append(" where 1 = 1");// 方便添加后续的条件
				// 追加sql
				if (map.containsKey("pdKeyLike")) {
					sql.append(" and P.KEY_ like #{pdKeyLike}");
				}
				if (map.containsKey("pdNameLike")) {
					sql.append(" and P.NAME_ like #{pdNameLike}");
				}
				if (map.containsKey("piNameLike")) {
					sql.append(" and RES.NAME_ like #{piNameLike}");
				}
				NativeProcessInstanceQuery q = runtimeService.createNativeProcessInstanceQuery();
				// 先查询数据
				q.sql("select distinct RES.*, P.KEY_ as ProcessDefinitionKey, P.ID_ as ProcessDefinitionId, P.NAME_ as ProcessDefinitionName, P.VERSION_ as ProcessDefinitionVersion, P.DEPLOYMENT_ID_ as DeploymentId " + sql);
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
				List<ProcessInstance> rows = q.listPage(SystemContext.getOffset(), SystemContext.getPageSize());
				// 再查询总数
				long total = q.sql("select count(distinct RES.ID_) " + sql).count();
				// 序列化查询结果为JSON
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("total", total);
				result.put("rows", rows);
				return om.writeValueAsString(result);
			} else if ("his".equals(type)) {
				// 查询执行结束的流程实例
				StringBuffer sql = new StringBuffer("from ACT_HI_PROCINST RES inner join ${prefix}ACT_RE_PROCDEF DEF on RES.PROC_DEF_ID_ = DEF.ID_");
				// 只查询结束的流程
				sql.append(" where RES.END_TIME_ is not NULL");
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
				result.put("rows", rows);
				return om.writeValueAsString(result);
			} else {
				// TODO: 预留扩展
				return "{ \"total\" : 0, \"rows\" : [] }";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
    }

}
