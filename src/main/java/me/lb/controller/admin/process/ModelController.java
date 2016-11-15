package me.lb.controller.admin.process;

import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@RequestMapping(value = "/admin/process/model")
public class ModelController {

	@Autowired
	private RepositoryService repositoryService;

	/**
	 * 创建模型
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
			// 存储流程模型的信息
			repositoryService.saveModel(model);
			repositoryService.addModelEditorSource(model.getId(), json.toString().getBytes("utf-8"));
			return "redirect:/admin/process/modeler/editor.html?modelId=" + model.getId();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable String id) {
		try {
			repositoryService.deleteModel(id);
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"操作失败！\" }";
		}
	}

}
