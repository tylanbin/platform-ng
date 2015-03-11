package me.lb.dao.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class CommonDaoTest {

	@Autowired
	private CommonDao commonDao;// 以User为例进行通用方法的测试

	@After
	public void cleanData() {
		// 清空全部数据
		commonDao.deleteAll();
	}

	@Test
	public void testFindById() {
		User u = new User("test", "test");
		int id = commonDao.save(u);
		Assert.assertEquals(u.getLoginName(), commonDao.findById(id)
				.getLoginName());
	}

	@Test
	public void testFindAll() {
		User u1 = new User("test1", "test");
		User u2 = new User("test2", "test");
		commonDao.save(u1);
		commonDao.save(u2);
		Assert.assertEquals(2, commonDao.findAll().size());
	}

	@Test
	public void testFindAllWithParams() {
		User u1 = new User("test1", "test");
		User u2 = new User("test2", "test");
		User u3 = new User("test3", "null");
		commonDao.save(u1);
		commonDao.save(u2);
		commonDao.save(u3);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginPwd", "test");
		Assert.assertEquals(2, commonDao.findAll(params).size());
	}

	@Test
	public void testSave() {
		User u = new User("test", "test");
		int id = commonDao.save(u);
		Assert.assertNotNull(commonDao.findById(id));
	}

	@Test
	public void testUpdate() {
		User u = new User("test", "test");
		int id = commonDao.save(u);
		u.setLoginPwd("null");
		commonDao.update(u);
		Assert.assertEquals("null", commonDao.findById(id).getLoginPwd());
	}

	@Test
	public void testDelete() {
		User u = new User("test", "test");
		int id = commonDao.save(u);
		Assert.assertNotNull(commonDao.findById(id));
		commonDao.delete(u);
		Assert.assertNull(commonDao.findById(id));
	}

	@Test
	public void testDeleteAll() {
		User u1 = new User("test1", "test");
		User u2 = new User("test2", "test");
		commonDao.save(u1);
		commonDao.save(u2);
		Assert.assertEquals(2, commonDao.findAll().size());
		commonDao.deleteAll();
		Assert.assertEquals(0, commonDao.findAll().size());
	}

	@Test
	public void testDeleteAllWithParams() {
		User u1 = new User("test1", "test");
		User u2 = new User("test2", "test");
		User u3 = new User("test3", "null");
		commonDao.save(u1);
		commonDao.save(u2);
		commonDao.save(u3);
		Assert.assertEquals(3, commonDao.findAll().size());
		List<User> users = new ArrayList<User>();
		users.add(u1);
		users.add(u2);
		commonDao.deleteAll(users);
		Assert.assertEquals(1, commonDao.findAll().size());
		Assert.assertEquals("null", commonDao.findAll().get(0).getLoginPwd());
	}

	@Test
	public void testPagingQuery() {
		User u1 = new User("test1", "test");
		User u2 = new User("test2", "test");
		commonDao.save(u1);
		commonDao.save(u2);
		SystemContext.setPageSize(1);
		Assert.assertEquals(2, commonDao.pagingQuery().getTotal());
		Assert.assertEquals(1, commonDao.pagingQuery().getDatas().size());
	}

	@Test
	public void testPagingQueryWithParams() {
		User u1 = new User("test1", "test");
		User u2 = new User("test2", "test");
		User u3 = new User("test3", "null");
		commonDao.save(u1);
		commonDao.save(u2);
		commonDao.save(u3);
		SystemContext.setPageSize(1);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginPwd", "test");
		Assert.assertEquals(2, commonDao.pagingQuery(params).getTotal());
		Assert.assertEquals(1, commonDao.pagingQuery(params).getDatas().size());
	}

}
