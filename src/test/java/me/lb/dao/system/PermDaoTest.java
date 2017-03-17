package me.lb.dao.system;

import me.lb.model.system.Perm;
import me.lb.support.jackson.JsonWriter;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@TransactionConfiguration(defaultRollback = true)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-orm.xml")
public class PermDaoTest {

	@Autowired
	private PermDao permDao;

	@After
	public void cleanData() {
		// 清空全部数据
		permDao.deleteAll();
	}

	@Test
	public void testFindTopPerms() throws Exception {
		Perm p1 = new Perm("用户管理", "user", "/user", null);
		int id1 = permDao.save(p1);
		Perm p11 = new Perm("添加用户", "user:add", "/user/add", id1);
		Perm p12 = new Perm("修改用户", "user:edit", "/user/edit", id1);
		Perm p13 = new Perm("删除用户", "user:delete", "/user/delete", id1);
		Perm p14 = new Perm("查询用户", "user:list", "/user/list", id1);
		permDao.save(p11);
		permDao.save(p12);
		permDao.save(p13);
		int id14 = permDao.save(p14);
		Perm p141 = new Perm("高级查询", "user:list:search", "/user/list/search", id14);
		Perm p142 = new Perm("导出数据", "user:list:export", "/user/list/export", id14);
		permDao.save(p141);
		permDao.save(p142);
		System.out.println(JsonWriter.getInstance().filter(Perm.class).getWriter()
				.withDefaultPrettyPrinter().writeValueAsString(permDao.findTopPerms()));
	}

}
