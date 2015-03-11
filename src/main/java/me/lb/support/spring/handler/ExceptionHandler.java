package me.lb.support.spring.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * 自定义异常处理器
 */
public class ExceptionHandler implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		// 不使用默认的ModelAndView
		try {
			PrintWriter out = response.getWriter();
			out.println(ex.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}