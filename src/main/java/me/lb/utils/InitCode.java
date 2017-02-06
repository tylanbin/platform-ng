package me.lb.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.lb.support.system.annotation.MetaData;

import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class InitCode {

	public static final String TEMPLATEPATH = "code-template";
	public static final String[] POJOFOLDER = { "demo" };

	public static void main(String[] args) throws Exception {
		// 先遍历所有的目录，得到全部的实体
		String basePath = System.getProperty("user.dir")
				+ "/src/main/java/me/lb/";
		for (String folderStr : POJOFOLDER) {
			// 各层的全路径
			String path_model = basePath + "model/" + folderStr;
			String path_Dao = basePath + "dao/" + folderStr;
			String path_Service = basePath + "service/" + folderStr;
			String path_controller = basePath + "controller/admin/" + folderStr;
			// model的包名称，反射时使用
			String packageName = "me.lb.model." + folderStr;
			File folder = new File(path_model);
			File[] pojos = folder.listFiles();
			for (File pojo : pojos) {
				// 读取到每一个实体文件，并得到类名
				String fileName = pojo.getName();
				String className = StringUtils.stripFilenameExtension(fileName);
				// 先生成Dao、Service、Controller
				generateDao(path_Dao, folderStr, className);
				generateService(path_Service, folderStr, className);
				generateController(path_controller, folderStr, className);
				// 反射类，获取字段及对应中文
				Map<String, String> fieldMap = new HashMap<String, String>();
				Class<?> clazz = Class.forName(packageName + "." + className);
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					MetaData meta = field.getAnnotation(MetaData.class);
					if (meta != null) {
						fieldMap.put(field.getName(), meta.chsName());
					}
				}
				// 生成页面
				generateWeb(folderStr, className, fieldMap);
			}
		}
	}

	private static void generateDao(String path, String category,
			String className) throws Exception {
		File folder_interface = new File(path);
		File folder_implclass = new File(path + "/impl");
		// 创建目录
		if (!folder_interface.exists()) {
			folder_interface.mkdirs();
		}
		if (!folder_implclass.exists()) {
			folder_implclass.mkdirs();
		}
		// 创建文件
		FileOutputStream output = null;
		File file_interface = new File(path + "/" + className + "Dao.java");
		File file_implclass = new File(path + "/impl/" + className
				+ "DaoImpl.java");
		// 写入interface
		if (!file_interface.exists()) {
			String objName = className.substring(0, 1).toLowerCase()
					+ className.substring(1);
			output = new FileOutputStream(file_interface);
			String content = generateSrc(category, className, objName,
					"dao.template");
			IOUtils.write(content, output, "utf-8");
		}
		// 写入class
		if (!file_implclass.exists()) {
			String objName = className.substring(0, 1).toLowerCase()
					+ className.substring(1);
			output = new FileOutputStream(file_implclass);
			String content = generateSrc(category, className, objName,
					"daoImpl.template");
			IOUtils.write(content, output, "utf-8");
		}
	}

	private static void generateService(String path, String category,
			String className) throws Exception {
		File folder_interface = new File(path);
		File folder_implclass = new File(path + "/impl");
		// 创建目录
		if (!folder_interface.exists()) {
			folder_interface.mkdirs();
		}
		if (!folder_implclass.exists()) {
			folder_implclass.mkdirs();
		}
		// 创建文件
		FileOutputStream output = null;
		File file_interface = new File(path + "/" + className + "Service.java");
		File file_implclass = new File(path + "/impl/" + className
				+ "ServiceImpl.java");
		// 写入interface
		if (!file_interface.exists()) {
			String objName = className.substring(0, 1).toLowerCase()
					+ className.substring(1);
			output = new FileOutputStream(file_interface);
			String content = generateSrc(category, className, objName,
					"service.template");
			IOUtils.write(content, output, "utf-8");
		}
		// 写入class
		if (!file_implclass.exists()) {
			String objName = className.substring(0, 1).toLowerCase()
					+ className.substring(1);
			output = new FileOutputStream(file_implclass);
			String content = generateSrc(category, className, objName,
					"serviceImpl.template");
			IOUtils.write(content, output, "utf-8");
		}
	}

	private static void generateController(String path, String category,
			String className) throws Exception {
		File folder = new File(path);
		// 创建目录
		if (!folder.exists()) {
			folder.mkdirs();
		}
		// 创建文件
		FileOutputStream output = null;
		File file = new File(path + "/" + className + "Controller.java");
		// 写入文件
		if (!file.exists()) {
			String objName = className.substring(0, 1).toLowerCase()
					+ className.substring(1);
			output = new FileOutputStream(file);
			String content = generateSrc(category, className, objName,
					"controller.template");
			IOUtils.write(content, output, "utf-8");
		}
	}

	private static void generateWeb(String category, String className,
			Map<String, String> fields) throws Exception {
		String objName = className.substring(0, 1).toLowerCase()
				+ className.substring(1);
		String path_html = System.getProperty("user.dir")
				+ "/src/main/webapp/web/admin/" + category + "/";
		String path_js = System.getProperty("user.dir")
				+ "/src/main/webapp/assets/admin/" + category + "/";
		File folder_html = new File(path_html + objName);
		File folder_js = new File(path_js + objName);
		// 创建目录
		if (!folder_html.exists()) {
			folder_html.mkdirs();
		}
		if (!folder_js.exists()) {
			folder_js.mkdirs();
		}
		// 创建文件
		FileOutputStream output = null;
		File file_html = new File(path_html + objName + "/list.html");
		File file_js = new File(path_js + objName + "/list.js");
		// 写入html
		if (!file_html.exists()) {
			output = new FileOutputStream(file_html);
			String content = generateHtml(category, className, objName,
					"web-html.template", fields);
			IOUtils.write(content, output, "utf-8");
		}
		// 写入js
		if (!file_js.exists()) {
			output = new FileOutputStream(file_js);
			String content = generateJs(category, className, objName,
					"web-js.template", fields);
			IOUtils.write(content, output, "utf-8");
		}
	}

	/**
	 * 生成源代码的方法
	 * @param category 与system平级的包名称
	 * @param upperCase 类名（首字母大写）
	 * @param lowerCase 对象名（首字母小写）
	 * @param templateName 模板名称
	 * @return 替换后的模板字符串
	 */
	private static String generateSrc(String category, String upperCase,
			String lowerCase, String templateName) throws Exception {
		String path = System.getProperty("user.dir") + "/src/main/resources/"
				+ TEMPLATEPATH + "/";
		File templateFile = new File(path + templateName);
		FileInputStream input = new FileInputStream(templateFile);
		String content = IOUtils.toString(input, "utf-8");
		String result = content.replaceAll("\\{Category\\}", category)
				.replaceAll("\\{UpperCase\\}", upperCase)
				.replaceAll("\\{LowerCase\\}", lowerCase);
		return result;
	}

	/**
	 * 生成html的方法
	 * @param category 与system平级的包名称
	 * @param upperCase 类名（首字母大写）
	 * @param lowerCase 对象名（首字母小写）
	 * @param templateName 模板名称
	 * @param fields 反射得到的属性map
	 * @return 替换后的模板字符串
	 */
	private static String generateHtml(String category, String upperCase,
			String lowerCase, String templateName, Map<String, String> fields)
			throws Exception {
		String path = System.getProperty("user.dir") + "/src/main/resources/"
				+ TEMPLATEPATH + "/";
		File templateFile = new File(path + templateName);
		FileInputStream input = new FileInputStream(templateFile);
		String content = IOUtils.toString(input, "utf-8");
		// 处理html
		String tp_search = "\t\t\t\t\t<div data-options=\"name:'{fname}'\">{cname}</div>\n";
		String tp_edit = "\t\t\t\t\t<tr><td style=\"text-align: right;\">{cname}：</td>"
				+ "<td><input type=\"text\" name=\"{fname}\" "
				+ "class=\"easyui-textbox\" data-options=\"required:true\" /></td></tr>\n";
		StringBuffer html_search = new StringBuffer();
		StringBuffer html_edit = new StringBuffer();
		Iterator<Map.Entry<String, String>> it = fields.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> me = it.next();
			String fname = me.getKey();// 英文
			String cname = me.getValue();// 中文
			// 处理搜索select的html
			html_search.append(tp_search.replaceAll("\\{fname\\}", fname)
					.replaceAll("\\{cname\\}", cname));
			// 处理编辑时table的html
			html_edit.append(tp_edit.replaceAll("\\{fname\\}", fname)
					.replaceAll("\\{cname\\}", cname));
		}
		// 处理模板
		String result = content.replaceAll("\\{Category\\}", category)
				.replaceAll("\\{UpperCase\\}", upperCase)
				.replaceAll("\\{LowerCase\\}", lowerCase)
				.replaceAll("\\{SearchHtml\\}", html_search.toString())
				.replaceAll("\\{EditHtml\\}", html_edit.toString());
		return result;
	}

	/**
	 * 生成js的方法
	 * @param category 与system平级的包名称
	 * @param upperCase 类名（首字母大写）
	 * @param lowerCase 对象名（首字母小写）
	 * @param templateName 模板名称
	 * @param fields 反射得到的属性map
	 * @return 替换后的模板字符串
	 */
	private static String generateJs(String category, String upperCase,
			String lowerCase, String templateName, Map<String, String> fields)
			throws Exception {
		String path = System.getProperty("user.dir") + "/src/main/resources/"
				+ TEMPLATEPATH + "/";
		ObjectMapper om = new ObjectMapper();
		File templateFile = new File(path + templateName);
		FileInputStream input = new FileInputStream(templateFile);
		String content = IOUtils.toString(input, "utf-8");
		// 处理json
		ArrayNode arr_list = om.createArrayNode();
		ArrayNode arr_add = om.createArrayNode();
		Iterator<Map.Entry<String, String>> it = fields.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> me = it.next();
			String fname = me.getKey();// 英文
			String cname = me.getValue();// 中文
			// 处理list的column
			ObjectNode obj_list = om.createObjectNode();
			obj_list.put("field", fname);
			obj_list.put("title", cname);
			arr_list.add(obj_list);
			// 处理add的column
			ObjectNode obj_add = om.createObjectNode();
			obj_add.put("field", fname);
			obj_add.put("title", cname);
			obj_add.put("width", 80);
			ObjectNode add_editor = om.createObjectNode();
			ObjectNode add_editor_options = om.createObjectNode();
			add_editor_options.put("required", true);
			add_editor.put("type", "textbox");
			add_editor.put("options", add_editor_options);
			obj_add.put("editor", add_editor);
			arr_add.add(obj_add);
		}
		// 处理模板
		String json_list = om.writerWithDefaultPrettyPrinter()
				.writeValueAsString(arr_list);// 优化格式
		String json_add = om.writerWithDefaultPrettyPrinter()
				.writeValueAsString(arr_add);// 优化格式
		String result = content.replaceAll("\\{Category\\}", category)
				.replaceAll("\\{UpperCase\\}", upperCase)
				.replaceAll("\\{LowerCase\\}", lowerCase)
				.replaceAll("\\{ListJson\\}", json_list)
				.replaceAll("\\{AddJson\\}", json_add);
		return result;
	}

}
