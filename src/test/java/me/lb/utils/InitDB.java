package me.lb.utils;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
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
	@Autowired
	private LocalSessionFactoryBean sessionFactory;

	@Test
	public void db_init() throws Exception {
		// 处理系统的表
		Configuration cfg = sessionFactory.getConfiguration();
		SchemaExport se = new SchemaExport(cfg);
		se.create(false, true);
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
