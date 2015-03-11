package me.lb.support.shiro.realm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import me.lb.model.system.Perm;
import me.lb.model.system.Role;
import me.lb.model.system.User;
import me.lb.service.system.UserService;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class SystemRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		// 权限验证
		// 获取登录用户
		String loginName = (String) principals.fromRealm(getName()).iterator()
				.next();
		User loginUser = userService.findByLoginName(loginName);
		// 取得用户的权限
		Set<Role> roles = loginUser.getRoles();
		Set<Perm> perms = new HashSet<Perm>();
		Iterator<Role> it_role = roles.iterator();
		while (it_role.hasNext()) {
			// 遍历角色，将每个角色拥有的权限放入到Set中进行重复过滤
			perms.addAll(it_role.next().getPerms());
		}
		// 放入SimpleAuthorizationInfo返回
		Set<String> permTokens = new HashSet<String>();
		Iterator<Perm> it_perm = perms.iterator();
		while (it_perm.hasNext()) {
			// 遍历权限标识，放入统一的集合
			permTokens.add(it_perm.next().getToken());
		}
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addStringPermissions(permTokens);
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {
		// 登录认证
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		String loginName = token.getUsername();
		User loginUser = userService.findByLoginName(loginName);
		if (loginUser != null) {
			return new SimpleAuthenticationInfo(loginUser.getLoginName(),
					loginUser.getLoginPwd(), getName());
		} else {
			return null;
		}
	}

	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(
				principal, getName());
		clearCachedAuthenticationInfo(principals);
	}

}
