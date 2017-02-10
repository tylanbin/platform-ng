package me.lb.utils;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;

/**
 * Activiti工具类
 * ActivitiUtil
 * @author lanbin
 * @date 2017-2-7
 */
public class ActivitiUtil {

	/**
	 * 将流程定义转换为Map
	 * @param pd Activiti的流程定义
	 * @return 封装后的Map
	 */
	public static Map<String, Object> convertProcessDefinitionToMap(ProcessDefinition pd) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", pd.getId());
		map.put("key", pd.getKey());
		map.put("name", pd.getName());
		map.put("version", pd.getVersion());
		map.put("category", pd.getCategory());
		map.put("deploymentId", pd.getDeploymentId());
		map.put("description", pd.getDescription());
		map.put("tenantId", pd.getTenantId());
		map.put("isSuspended", pd.isSuspended());
		map.put("hasStartFormKey", pd.hasStartFormKey());
		return map;
	}
	
	/**
	 * 将任务实例转换为Map
	 * @param task 任务实例
	 * @return 封装后的Map
	 */
	public static Map<String, Object> convertTaskToMap(Task task) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", task.getId());
		map.put("taskDefinitionKey", task.getTaskDefinitionKey());
		map.put("name", task.getName());
		map.put("createTime", task.getCreateTime());
		map.put("processDefinitionId", task.getProcessDefinitionId());
		map.put("processInstanceId", task.getProcessInstanceId());
		map.put("formKey", task.getFormKey());
		map.put("description", task.getDescription());
		map.put("tenantId", task.getTenantId());
		map.put("assignee", task.getAssignee());
		return map;
	}

}
