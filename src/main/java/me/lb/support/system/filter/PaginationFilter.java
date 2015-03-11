package me.lb.support.system.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import me.lb.support.system.SystemContext;

/**
 * 实现分页的过滤器
 */
public class PaginationFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		// page-当前要显示的页，rows-每页要显示的条目数量（EasyUI）
		HttpServletRequest request = (HttpServletRequest) req;
		String pageNumStr = request.getParameter("page");
		String pageSizeStr = request.getParameter("rows");
		try {
			if (!StringUtils.isEmpty(pageNumStr)
					&& !StringUtils.isEmpty(pageSizeStr)) {
				int pageNum = Integer.parseInt(pageNumStr);
				int pageSize = Integer.parseInt(pageSizeStr);
				int offset = (pageNum - 1) * pageSize;
				SystemContext.setOffset(offset);
				SystemContext.setPageSize(pageSize);
			}
			chain.doFilter(req, res);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SystemContext.removeOffset();
			SystemContext.removePageSize();
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
