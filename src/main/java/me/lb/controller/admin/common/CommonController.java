package me.lb.controller.admin.common;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import me.lb.model.system.Emp;
import me.lb.model.system.User;
import me.lb.support.jackson.JsonWriter;
import me.lb.utils.UserUtil;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommonController {

	@ResponseBody
	@RequestMapping(value = "/getLoginInfo")
	public String getLoginInfo(HttpSession session) {
		try {
			User u = UserUtil.getUserFromSession(session);
			return JsonWriter.getInstance()
					.filter(User.class, "roles")
					.filter(Emp.class, "org", "users")
					.getWriter().writeValueAsString(u);
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
	}
	
	@RequestMapping(value = "/favicon.ico")
	public void getFavicon(HttpServletResponse response) {
		try {
			InputStream input = this.getClass().getClassLoader().getResourceAsStream("favicon.ico");
			response.setContentType("image/x-icon");
			IOUtils.copy(input, response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}