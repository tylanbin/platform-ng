package me.lb.controller.admin.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lb.support.system.SystemContext;

import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.NativeModelQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
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
			return "redirect:/web/admin/process/model/editor.html?id=" + model.getId();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/batch", method = RequestMethod.DELETE)
	public String batch_delete(String ids) {
		// 批量删除的操作
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
		ObjectMapper om = new ObjectMapper();
		try {
			Map<String, Object> map = null;
			if (!StringUtils.isEmpty(params)) {
				map = om.readValue(params, new TypeReference<Map<String, Object>>() {});
			}
			// 处理参数，拼接sql
			StringBuffer sql = new StringBuffer("from ACT_RE_MODEL RES where 1=1");
			if (map != null && map.containsKey("nameLike")) {
				sql.append(" and RES.NAME_ like #{nameLike}");
			}
			if (map != null && map.containsKey("keyLike")) {
				sql.append(" and RES.KEY_ like #{keyLike}");
			}
			// 创建自定义查询
			NativeModelQuery q = repositoryService.createNativeModelQuery().sql("select distinct RES.* " + sql);
			if (map != null && map.containsKey("nameLike")) {
				q.parameter("nameLike", "%" + map.get("nameLike") + "%");
			}
			if (map != null && map.containsKey("keyLike")) {
				q.parameter("keyLike", "%" + map.get("keyLike") + "%");
			}
			// 开始查询结果
			List<Model> rows = q.listPage(SystemContext.getOffset(), SystemContext.getPageSize());
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
