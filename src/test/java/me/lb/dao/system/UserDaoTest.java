package me.lb.dao.system;

import java.util.HashMap;
import java.util.Map;

import me.lb.model.system.User;
import me.lb.support.system.SystemContext;

import org.junit.After;
import org.junit.Assert;
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
public class UserDaoTest {

	@Autowired
	private UserDao userDao;

	@After
	public void cleanData() {
		// 清空全部数据
		userDao.deleteAll();
	}

	@Test
	public void testPagingQuery() {
		User u1 = new User("test1", "test");
		User u2 = new User("test2", "test");
		userDao.save(u1);
		userDao.save(u2);
		SystemContext.setPageSize(1);
		Assert.assertEquals(2, userDao.pagingQuery().getTotal());
		Assert.assertEquals(1, userDao.pagingQuery().getDatas().size());
	}

	@Test
	public void testPagingQueryWithParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginPwd", "test");
		Assert.assertNull(userDao.pagingQuery(params));
	}

}
