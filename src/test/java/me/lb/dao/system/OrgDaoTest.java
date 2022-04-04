package me.lb.dao.system;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Rollback(false)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-orm.xml")
public class OrgDaoTest {

	@Autowired
	private OrgDao orgDao;

	@After
	public void cleanData() {
		// 清空全部数据
		orgDao.deleteAll();
	}

	@Test
	public void testPagingQuery() {
	}

	@Test
	public void testPagingQueryWithParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("xxx", "xxx");
	}

	@Test
	public void testFindTopOrgs() {
	}

}
