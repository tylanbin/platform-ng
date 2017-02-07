package me.lb.controller.admin.process;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletResponse;

import me.lb.support.system.SystemContext;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(value = "/admin/process/def")
public class DefController {
	
	@Autowired
	private RepositoryService repositoryService;
	
	/**
	 * 通过zip文件部署流程
	 * @param name 流程名称
	 * @param file 上传的zip文件
	 */
	@ResponseBody
	@RequestMapping(value = "/deploy", method = RequestMethod.POST)
	public String deploy(MultipartFile file) {
		try {
			String fileName = file.getOriginalFilename();
			if ("zip".equals(FilenameUtils.getExtension(fileName))) {
				ZipInputStream zip = new ZipInputStream(file.getInputStream());
				Deployment d = repositoryService.createDeployment().name(fileName).addZipInputStream(zip).deploy();
				// 查询部署信息
	            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().deploymentId(d.getId()).singleResult();
	            return "{ \"success\" : true, \"processDefinitionId\" : \"" + pd.getId() + "\" }";
			} else {
				// 只允许部署zip类型的文件
				return "{ \"msg\" : \"部署失败，只允许使用zip文件格式！\" }";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"上传失败！\" }";
		}
	}

	/**
	 * 批量删除流程部署
	 * @param ids deploymentId的集合
	 */
	@ResponseBody
	@RequestMapping(value = "/batch", method = RequestMethod.DELETE)
	public String batch_delete(String ids) {
		try {
			String[] temp = ids.split(",");
			for (String id : temp) {
				// 删除流程的部署和定义，并删除流程实例
				repositoryService.deleteDeployment(id, true);
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}
	
	/**
	 * 挂起/激活流程定义
	 * @param pdId 流程定义id
	 * @param type 操作类型：active/suspend
	 */
	@ResponseBody
	@RequestMapping(value = "/{pdId}/state/{type}", method = RequestMethod.PUT)
	public String state(@PathVariable String pdId, @PathVariable String type) {
		try {
			if ("active".equals(type)) {
				repositoryService.activateProcessDefinitionById(pdId);
			} else if ("suspend".equals(type)) {
				repositoryService.suspendProcessDefinitionById(pdId);
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}
	
	/**
	 * 获取流程定义的资源文件
	 * @param pdId 流程定义id
	 * @param type 资源文件类型：xml/img
	 */
	@RequestMapping(value = "/{pdId}/resource/{type}", method = RequestMethod.GET)
	public void resource(@PathVariable String pdId, @PathVariable String type, HttpServletResponse response) {
		try {
			// 获取流程定义
			ProcessDefinition pd = repositoryService.getProcessDefinition(pdId);
			// 获取资源名称
			String resourceName = null;
			if ("xml".equals(type)) {
				response.setContentType("application/xml");
				resourceName = pd.getResourceName();
			} else {
				response.setContentType("image/png");
				resourceName = pd.getDiagramResourceName();
			}
			// 获取资源内容（xml/image）并返回
			InputStream input = repositoryService.getResourceAsStream(pd.getDeploymentId(), resourceName);
			IOUtils.copy(input, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/data/{type}", method = RequestMethod.GET)
	public String data(@PathVariable String type, String params) {
		try {
			// 先不进行公司的区分
			ObjectMapper om = new ObjectMapper();
			// 处理查询参数
			Map<String, Object> map = new HashMap<String, Object>();
			if (!StringUtils.isEmpty(params)) {
				map = om.readValue(params, new TypeReference<Map<String, Object>>() {});
			}
			// 分类别查询数据（这里只显示最新版本，去掉latestVersion显示全部）
			ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery().latestVersion();
			if ("all".equals(type)) {
				// 查询全部流程定义
			} else if ("active".equals(type)) {
				query = query.active();
			} else if ("suspended".equals(type)) {
				query = query.suspended();
			}
			// 级联查询参数
			if (map.containsKey("pdKeyLike")) {
				query = query.processDefinitionKeyLike("%" + map.get("pdKey") + "%");
			}
			if (map.containsKey("pdNameLike")) {
				query = query.processDefinitionNameLike("%" + map.get("pdName") + "%");
			}
			if (map.containsKey("pdCategoryLike")) {
				query = query.processDefinitionCategoryLike("%" + map.get("pdCategory") + "%");
			}
			// 查询结果排序
			query = query.orderByProcessDefinitionKey().desc();
			// 查询结果（分页查询）
			long total = query.count();
			List<ProcessDefinition> list = query.listPage(SystemContext.getOffset(), SystemContext.getPageSize());
			// 直接使用该list会出现异常（Direct self-reference leading to cycle），所以需要使用值对象进行处理
			List<Object> datas = new ArrayList<Object>();
			for (ProcessDefinition pd : list) {
				datas.add(convert(pd));
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
	
	// 私有方法
	
	/**
	 * 将流程定义转换为Map
	 * @param pd Activiti的流程定义
	 * @return 封装后的Map
	 */
	private Map<String, Object> convert(ProcessDefinition pd) {
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
