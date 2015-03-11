package me.lb.dao;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-hibernate.xml")
public class DBInit {

	@Autowired
	private LocalSessionFactoryBean sessionFactory;

	@Test
	public void init() {
		Configuration cfg = sessionFactory.getConfiguration();
		SchemaExport se = new SchemaExport(cfg);
		se.create(false, true);
	}

}
