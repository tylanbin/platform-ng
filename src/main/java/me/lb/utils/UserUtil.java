package me.lb.utils;

import javax.servlet.http.HttpSession;

import me.lb.model.system.User;

/**
 * 用户工具类
 * UserUtil
 * @author lanbin
 * @date 2017-2-3
 */
public class UserUtil {

	public static final String ATTRNAME = "user";

	public static void saveUserToSession(User user, HttpSession session) {
		session.setAttribute(ATTRNAME, user);
	}

	public static User getUserFromSession(HttpSession session) {
		Object obj = session.getAttribute(ATTRNAME);
		return obj == null ? null : (User) obj;
	}

}
