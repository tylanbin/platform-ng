package me.lb.controller.admin.process;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletResponse;

import me.lb.support.system.SystemContext;
import me.lb.utils.ActivitiUtil;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.NativeProcessDefinitionQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;
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
	 * 为流程定义设置发起人/角色
	 * @param pdIds 流程定义集合
	 * @param ids 设置的角色id集合
	 */
	@ResponseBody
	@RequestMapping(value = "/{pdId}/auth", method = RequestMethod.POST)
	public String auth(@PathVariable String pdId, String ids) {
		try {
			// 设置之前需要全部先清除
			List<IdentityLink> olds = repositoryService.getIdentityLinksForProcessDefinition(pdId);
			for (IdentityLink il : olds) {
				if (IdentityLinkType.ASSIGNEE.equals(il.getType())) {
					repositoryService.deleteCandidateStarterUser(pdId, il.getUserId());
				} else {
					repositoryService.deleteCandidateStarterGroup(pdId, il.getGroupId());
				}
			}
			// 现在默认只分配角色
			String[] temp = ids.split(",");
			for (String id : temp) {
				repositoryService.addCandidateStarterGroup(pdId, id);
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"上传失败！\" }";
		}
	}

	/**
	 * 获取流程定义已设置的发起角色
	 * @param pdId 流程定义id
	 */
	@ResponseBody
	@RequestMapping(value = "/{pdId}/auth", method = RequestMethod.GET)
	public String getAuth(@PathVariable String pdId) {
		try {
			ObjectMapper om = new ObjectMapper();
			List<IdentityLink> olds = repositoryService.getIdentityLinksForProcessDefinition(pdId);
			List<Integer> ids = new ArrayList<Integer>();
			for (IdentityLink il : olds) {
				ids.add(Integer.valueOf(il.getGroupId()));
			}
			return om.writeValueAsString(ids);
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
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
			// 由于封装的查询不支持latestVersion与nameLike并列，所以这里改为自定义查询
			// 编写的sql需要参考activiti-engine-x.x.x.jar中的org.activiti.db.mapping包
			StringBuffer sql = new StringBuffer("from ACT_RE_PROCDEF RES where 1=1");
			// 最新的版本
			sql.append(" and RES.VERSION_ = (select max(VERSION_) from ACT_RE_PROCDEF where KEY_ = RES.KEY_)");
			// 分类别
			if ("all".equals(type)) {
				// 查询全部流程定义
			} else if ("active".equals(type)) {
				sql.append(" and RES.SUSPENSION_STATE_ = 1");
			} else if ("suspended".equals(type)) {
				sql.append(" and RES.SUSPENSION_STATE_ = 2");
			}
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
	
}
