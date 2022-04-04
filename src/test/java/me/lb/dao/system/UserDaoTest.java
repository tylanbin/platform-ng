package me.lb.dao.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import me.lb.model.system.User;
import me.lb.support.system.SystemContext;

@Transactional
@Rollback(true)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-orm.xml")
public class UserDaoTest {

	@Autowired
	private UserDao userDao;

	@After
	public void cleanData() {
		// 清空全部数据
		userDao.deleteAll();
	}

	@Test
	public void testFindById() {
		User u = new User("test", "test");
		int id = userDao.save(u);
		Assert.assertEquals(u.getLoginName(), userDao.findById(id).getLoginName());
	}

	@Test
	public void testFindAll() {
		User u1 = new User("test1", "test");
		User u2 = new User("test2", "test");
		userDao.save(u1);
		userDao.save(u2);
		Assert.assertEquals(2, userDao.findAll().size());
	}

	@Test
	public void testFindAllWithParams() {
		User u1 = new User("test1", "test");
		User u2 = new User("test2", "test");
		User u3 = new User("test3", "null");
		userDao.save(u1);
		userDao.save(u2);
		userDao.save(u3);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginPass", "test");
		Assert.assertEquals(2, userDao.findAll(params).size());
	}

	@Test
	public void testSave() {
		User u = new User("test", "test");
		int id = userDao.save(u);
		Assert.assertNotNull(userDao.findById(id));
	}

	@Test
	public void testUpdate() {
		User u = new User("test", "test");
		int id = userDao.save(u);
		u.setLoginPass("null");
		userDao.update(id, u);
		Assert.assertEquals("null", userDao.findById(id).getLoginPass());
	}

	@Test
	public void testDelete() {
		User u = new User("test", "test");
		int id = userDao.save(u);
		Assert.assertNotNull(userDao.findById(id));
		userDao.delete(id);
		Assert.assertNull(userDao.findById(id));
	}

	@Test
	public void testDeleteAll() {
		User u1 = new User("test1", "test");
		User u2 = new User("test2", "test");
		userDao.save(u1);
		userDao.save(u2);
		Assert.assertEquals(2, userDao.findAll().size());
		userDao.deleteAll();
		Assert.assertEquals(0, userDao.findAll().size());
	}

	@Test
	public void testDeleteAllWithParams() {
		User u1 = new User("test1", "test");
		User u2 = new User("test2", "test");
		User u3 = new User("test3", "null");
		int id1 = userDao.save(u1);
		int id2 = userDao.save(u2);
		userDao.save(u3);
		Assert.assertEquals(3, userDao.findAll().size());
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(id1);
		ids.add(id2);
		userDao.deleteAll(ids);
		Assert.assertEquals("null", userDao.findAll().get(0).getLoginPass());
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
		User u1 = new User("test1", "test");
		User u2 = new User("test2", "test");
		User u3 = new User("test3", "null");
		userDao.save(u1);
		userDao.save(u2);
		userDao.save(u3);
		SystemContext.setPageSize(1);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("loginPass", "test");
		Assert.assertEquals(2, userDao.pagingQuery(params).getTotal());
		Assert.assertEquals(1, userDao.pagingQuery(params).getDatas().size());
	}

}
