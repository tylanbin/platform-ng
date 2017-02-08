package me.lb.utils;

/**
 * 这个生成代码的类可以不放在test中，放在这里是为了与数据生成统一
 * InitCode
 * @author lanbin
 * @date 2017-2-8
 */
public class InitCode {

	public static void main(String[] args) throws Exception {
		// 配置需要生成代码的实体java文件所在的目录名称
		String[] folders = { "demo" };
		CodeUtil.genAll(folders);
	}

}
