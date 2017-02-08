package me.lb.controller.admin.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import me.lb.support.system.SystemContext;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.NativeModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@RequestMapping(value = "/admin/process/model")
public class ModelController {
	
	@Autowired
	private RepositoryService repositoryService;

	/**
	 * 创建流程模型
	 * @param name 名称
	 * @param key KEY
	 * @param description 描述信息
	 */
	@RequestMapping(value = "add", method = RequestMethod.POST)
	public String add(String name, String key, String description) {
		try {
			ObjectMapper om = new ObjectMapper();
			// 创建流程模型
			Model model = repositoryService.newModel();
			ObjectNode metainfo = om.createObjectNode();
			metainfo.put(ModelDataJsonConstants.MODEL_NAME, name);
			metainfo.put(ModelDataJsonConstants.MODEL_REVISION, 1);
			metainfo.put(ModelDataJsonConstants.MODEL_DESCRIPTION, StringUtils.defaultString(description));
			model.setName(name);
			model.setKey(StringUtils.defaultString(key));
			model.setMetaInfo(metainfo.toString());
			// 创建流程模型的初始json数据（用于设计器）
			ObjectNode json = om.createObjectNode();
			json.put("id", "canvas");
			json.put("resourceId", "canvas");
			ObjectNode stencilset = om.createObjectNode();
			stencilset.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
			json.put("stencilset", stencilset);
			// 这里存储的信息在流程部署时会使用
			ObjectNode properties = om.createObjectNode();
			properties.put("name", name);// 流程名称
			properties.put("process_id", key);// 流程定义key
			// TODO: 流程Category
			// properties.put("process_namespace", description);
			properties.put("documentation", description);// 流程描述信息
			json.put("properties", properties);
			// 存储流程模型的信息
			repositoryService.saveModel(model);
			repositoryService.addModelEditorSource(model.getId(), json.toString().getBytes("utf-8"));
			return "redirect:/web/admin/process/model/editor.html?id=" + model.getId();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 部署流程模型为流程定义
	 * @param id 流程模型id
	 */
	@ResponseBody
	@RequestMapping(value = "/{id}/deploy", method = RequestMethod.POST)
	public String deploy(@PathVariable String id) {
		try {
			// 读取数据
			Model model = repositoryService.getModel(id);
            byte[] editorSource = repositoryService.getModelEditorSource(id);
            // 流程模型Json -> 流程模型 -> XML
            JsonNode jsonNode = new ObjectMapper().readTree(editorSource);
            BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(jsonNode);
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
            // 部署流程
            String xmlName = model.getName() + ".bpmn20.xml";
            Deployment d = repositoryService.createDeployment().name(model.getName()).addString(xmlName, new String(bpmnBytes, "utf-8")).deploy();
            // 查询部署信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().deploymentId(d.getId()).singleResult();
            return "{ \"success\" : true, \"processDefinitionId\" : \"" + pd.getId() + "\" }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"部署失败！\" }";
		}
	}
	
	/**
	 * 获取流程模型的Json/XML
	 * @param id 流程模型id
	 */
	@RequestMapping(value = "/{id}/resource/{type}", method = RequestMethod.GET)
	public void getJson(@PathVariable String id, String type, HttpServletResponse response) {
		try {
			Model model = repositoryService.getModel(id);
            byte[] editorSource = repositoryService.getModelEditorSource(id);
            if ("json".equals(type)) {
                IOUtils.write(editorSource, response.getWriter(), "utf-8");
                // 设置响应的信息
                String filename = new String((model.getName() + ".json").getBytes(), "iso-8859-1");
                response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			} else {
				// 流程模型Json -> 流程模型 -> XML
	            JsonNode jsonNode = new ObjectMapper().readTree(editorSource);
	            BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(jsonNode);
	            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
	            IOUtils.write(bpmnBytes, response.getWriter(), "utf-8");
	            // 设置响应的信息
	            String filename = new String((model.getName() + ".bpmn20.xml").getBytes(), "iso-8859-1");
	            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			}
            response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 批量删除流程模型
	 * @param ids id的集合串：1,5,6,7
	 */
	@ResponseBody
	@RequestMapping(value = "/batch", method = RequestMethod.DELETE)
	public String batch_delete(String ids) {
		try {
			String[] temp = ids.split(",");
			for (String id : temp) {
				repositoryService.deleteModel(id);
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
		// 查询数据集合的方法
		try {
			ObjectMapper om = new ObjectMapper();
			Map<String, Object> map = null;
			if (!StringUtils.isEmpty(params)) {
				map = om.readValue(params, new TypeReference<Map<String, Object>>() {});
			}
			// 处理参数，拼接sql
			// 编写的sql需要参考activiti-engine-x.x.x.jar中的org.activiti.db.mapping包
			StringBuffer sql = new StringBuffer("from ACT_RE_MODEL RES where 1=1");
			if (map != null && map.containsKey("nameLike")) {
				sql.append(" and RES.NAME_ like #{nameLike}");
			}
			if (map != null && map.containsKey("keyLike")) {
				sql.append(" and RES.KEY_ like #{keyLike}");
			}
			// 创建自定义查询
			NativeModelQuery q = repositoryService.createNativeModelQuery();
			// 先查询数据
			q.sql("select distinct RES.* " + sql);
			if (map != null && map.containsKey("nameLike")) {
				q.parameter("nameLike", "%" + map.get("nameLike") + "%");
			}
			if (map != null && map.containsKey("keyLike")) {
				q.parameter("keyLike", "%" + map.get("keyLike") + "%");
			}
			List<Model> rows = q.listPage(SystemContext.getOffset(), SystemContext.getPageSize());
			// 再查询总数
			long total = q.sql("select count(distinct RES.ID_) " + sql).count();
			// 序列化查询结果为JSON
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", total);
			result.put("rows", rows);
			// 不是自己的实体类，不需要进行输出过滤
			return om.writeValueAsString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"total\" : 0, \"rows\" : [] }";
		}
	}

}
