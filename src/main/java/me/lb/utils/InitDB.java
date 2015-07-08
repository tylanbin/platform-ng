package me.lb.utils;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

public class InitDB {

	public static void main(String[] args) {
		BeanFactory factory = new ClassPathXmlApplicationContext(
				"applicationContext-hibernate.xml");
		LocalSessionFactoryBean sessionFactory = factory
				.getBean(LocalSessionFactoryBean.class);
		Configuration cfg = sessionFactory.getConfiguration();
		SchemaExport se = new SchemaExport(cfg);
		se.create(false, true);
	}

}
