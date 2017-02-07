package me.lb.utils;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.repository.ProcessDefinition;

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

}
