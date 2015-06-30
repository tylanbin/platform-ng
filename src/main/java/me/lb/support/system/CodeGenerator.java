package me.lb.support.system;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

public class CodeGenerator {

	public static final String[] POJOFOLDER = { "me/lb/model/demo" };

	public static void main(String[] args) throws Exception {
		// 先遍历所有的目录，得到全部的实体
		String basePath = System.getProperty("user.dir") + "/src/main/java/";
		for (String folderStr : POJOFOLDER) {
			String path_model = basePath + folderStr;
			String path_Dao = path_model.replace("/model", "/dao");
			String path_Service = path_model.replace("/model", "/service");
			String packageName = folderStr.replaceAll("/", ".");
			File folder = new File(path_model);
			File[] pojos = folder.listFiles();
			for (File pojo : pojos) {
				// 读取到每一个实体文件，并得到类名
				String fileName = pojo.getName();
				String className = StringUtils.stripFilenameExtension(fileName);
				// 先生成Dao、Service、Controller
				generateDao(path_Dao, packageName, className);
				generateService(path_Service, packageName, className);
				// 反射对象
				// Class<?> clazz = Class.forName(packageName + "." + className);
			}
		}
	}

	private static void generateDao(String path, String modelPackage,
			String className) throws Exception {
		String packageName = modelPackage.replace(".model", ".dao");
		// 创建File
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
		StringBuffer sb = null;
		File file_interface = new File(path + "/" + className + "Dao.java");
		File file_implclass = new File(path + "/impl/" + className
				+ "DaoImpl.java");
		// 写入interface
		if (!file_interface.exists()) {
			output = new FileOutputStream(file_interface);
			sb = new StringBuffer();
			sb.append("package " + packageName + ";\n");
			sb.append("\n");
			sb.append("import me.lb.dao.common.GenericDao;\n");
			sb.append("import " + modelPackage + "." + className + ";\n");
			sb.append("\n");
			sb.append("public interface " + className
					+ "Dao extends GenericDao<" + className + ", Integer> {\n");
			sb.append("\n");
			sb.append("}\n");
			IOUtils.write(sb.toString(), output);
		}
		// 写入class
		if (!file_implclass.exists()) {
			output = new FileOutputStream(file_implclass);
			sb = new StringBuffer();
			sb.append("package " + packageName + ".impl;\n");
			sb.append("\n");
			sb.append("import java.util.Map;\n");
			sb.append("import org.springframework.stereotype.Repository;\n");
			sb.append("import me.lb.dao.common.impl.GenericDaoImpl;\n");
			sb.append("import " + packageName + "." + className + "Dao;\n");
			sb.append("import me.lb.model.pagination.Pagination;\n");
			sb.append("import " + modelPackage + "." + className + ";\n");
			sb.append("\n");
			sb.append("@Repository\n");
			sb.append("public class " + className
					+ "DaoImpl extends GenericDaoImpl<" + className
					+ ", Integer> implements " + className + "Dao {\n");
			sb.append("\n");
			sb.append("\t@Override\n");
			sb.append("\tpublic Pagination<" + className
					+ "> pagingQuery() {\n");
			sb.append("\t\treturn getPagination(\"from " + className
					+ "\", null);\n");
			sb.append("\t}\n");
			sb.append("\n");
			sb.append("\t@Override\n");
			sb.append("\tpublic Pagination<" + className
					+ "> pagingQuery(Map<String, Object> params) {\n");
			sb.append("\t\t// 不使用的话可以不实现\n");
			sb.append("\t\treturn null;\n");
			sb.append("\t}\n");
			sb.append("\n");
			sb.append("}\n");
			IOUtils.write(sb.toString(), output);
		}
	}

	private static void generateService(String path, String modelPackage,
			String className) throws Exception {
		String packageName = modelPackage.replace(".model", ".service");
		// 创建File
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
		StringBuffer sb = null;
		File file_interface = new File(path + "/" + className + "Service.java");
		File file_implclass = new File(path + "/impl/" + className
				+ "ServiceImpl.java");
		// 写入interface
		if (!file_interface.exists()) {
			output = new FileOutputStream(file_interface);
			sb = new StringBuffer();
			sb.append("package " + packageName + ";\n");
			sb.append("\n");
			sb.append("import me.lb.service.common.GenericService;\n");
			sb.append("import " + modelPackage + "." + className + ";\n");
			sb.append("\n");
			sb.append("public interface " + className
					+ "Service extends GenericService<" + className
					+ ", Integer> {\n");
			sb.append("\n");
			sb.append("}\n");
			IOUtils.write(sb.toString(), output);
		}
		// 写入class
		if (!file_implclass.exists()) {
			output = new FileOutputStream(file_implclass);
			sb = new StringBuffer();
			sb.append("package " + packageName + ".impl;\n");
			sb.append("\n");
			sb.append("import org.springframework.stereotype.Service;\n");
			sb.append("import me.lb.service.common.impl.GenericServiceImpl;\n");
			sb.append("import " + packageName + "." + className + "Service;\n");
			sb.append("import " + modelPackage + "." + className + ";\n");
			sb.append("\n");
			sb.append("@Service\n");
			sb.append("public class " + className
					+ "ServiceImpl extends GenericServiceImpl<" + className
					+ ", Integer> implements " + className + "Service {\n");
			sb.append("\n");
			sb.append("}\n");
			IOUtils.write(sb.toString(), output);
		}
	}

}
