package me.lb.dao.system;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@TransactionConfiguration(defaultRollback = false)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-hibernate.xml")
public class EmpDaoTest {

	@Autowired
	private EmpDao empDao;

	@After
	public void cleanData() {
		// 清空全部数据
		empDao.deleteAll();
	}

	@Test
	public void testPagingQuery() {
	}

	@Test
	public void testPagingQueryWithParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("xxx", "xxx");
	}

}
