package me.lb.support.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import me.lb.support.system.annotation.MetaData;

import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

public class CodeGenerator {

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
				// TODO: 生成页面

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
			output = new FileOutputStream(file_interface);
			String content = generateContent(category, className,
					className.toLowerCase(), "dao.template");
			IOUtils.write(content, output);
		}
		// 写入class
		if (!file_implclass.exists()) {
			output = new FileOutputStream(file_implclass);
			String content = generateContent(category, className,
					className.toLowerCase(), "daoImpl.template");
			IOUtils.write(content, output);
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
			output = new FileOutputStream(file_interface);
			String content = generateContent(category, className,
					className.toLowerCase(), "service.template");
			IOUtils.write(content, output);
		}
		// 写入class
		if (!file_implclass.exists()) {
			output = new FileOutputStream(file_implclass);
			String content = generateContent(category, className,
					className.toLowerCase(), "serviceImpl.template");
			IOUtils.write(content, output);
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
			output = new FileOutputStream(file);
			String content = generateContent(category, className,
					className.toLowerCase(), "controller.template");
			IOUtils.write(content, output);
		}
	}

	private static String generateContent(String category, String upperCase,
			String lowerCase, String templateName) throws Exception {
		String path = System.getProperty("user.dir") + "/src/main/resources/"
				+ TEMPLATEPATH + "/";
		File templateFile = new File(path + templateName);
		FileInputStream input = new FileInputStream(templateFile);
		String content = IOUtils.toString(input);
		String result = content.replaceAll("\\{Category\\}", category)
				.replaceAll("\\{UpperCase\\}", upperCase)
				.replaceAll("\\{LowerCase\\}", lowerCase);
		return result;
	}

}
