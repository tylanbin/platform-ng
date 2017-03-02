package me.lb.utils;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@TransactionConfiguration(defaultRollback = false)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-orm.xml")
public class InitDB {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void db_init() throws Exception {
		// 处理系统的表
		// FIXME
		// 处理Activiti的表
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("sql/activiti.sql");
		List<String> sqls = IOUtils.readLines(input, "utf-8");
		if (!sqls.isEmpty()) {
			jdbcTemplate.batchUpdate(sqls.toArray(new String[sqls.size()]));
		}
	}

	@Test
	public void data_init() throws Exception {
		// 测试数据
		String sqlName = "test.sql";
		// 正式数据
		// String sqlName = "normal.sql";
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("sql/" + sqlName);
		List<String> sqls = IOUtils.readLines(input, "utf-8");
		if (!sqls.isEmpty()) {
			jdbcTemplate.batchUpdate(sqls.toArray(new String[sqls.size()]));
		}
	}

}
