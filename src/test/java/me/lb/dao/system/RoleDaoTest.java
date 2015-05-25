package me.lb.dao.system;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.lb.model.system.Perm;
import me.lb.model.system.Role;

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
public class RoleDaoTest {

	@Autowired
	private RoleDao roleDao;
	@Autowired
	private PermDao permDao;

	@After
	public void cleanData() {
		// 清空全部数据
		roleDao.deleteAll();
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
	public void testFindTopRoles() {
	}

	@Test
	public void testSaveRolePerms() {
		// 先存入两个权限
		Perm perm1 = new Perm("perm1", "", "");
		Perm perm2 = new Perm("perm2", "", "");
		int id1 = permDao.save(perm1);
		int id2 = permDao.save(perm2);
		// 再存储角色
		Role role = new Role("test");
		Set<Perm> perms = new HashSet<Perm>();
		perms.add(permDao.findById(id1));
		perms.add(permDao.findById(id2));
		// 如果没有事先存储Perm，在存储Role时也会自动存储
		// perms.add(perm1);
		// perms.add(perm2);
		role.setPerms(perms);
		roleDao.save(role);
	}

}
