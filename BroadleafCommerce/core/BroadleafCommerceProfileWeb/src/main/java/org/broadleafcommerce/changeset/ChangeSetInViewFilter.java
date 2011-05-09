package org.broadleafcommerce.changeset;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.changeset.dao.ChangeSetDao;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class ChangeSetInViewFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		ChangeSetDao changeSetDao = (ChangeSetDao) wac.getBean("blChangeSetDao");
		ChangeSetThreadLocal.setChangeSetDao(changeSetDao);
		ChangeSetThreadLocal.setChangeSet(1L);
		ChangeSetThreadLocal.setUser(1L);
		
		try {
			filterChain.doFilter(request, response);
		} finally {
			ChangeSetThreadLocal.setChangeSetDao(null);
			ChangeSetThreadLocal.setChangeSet(null);
			ChangeSetThreadLocal.setUser(null);
		}
	}

}
