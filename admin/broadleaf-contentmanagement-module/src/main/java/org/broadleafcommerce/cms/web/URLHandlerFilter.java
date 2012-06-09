package org.broadleafcommerce.cms.web;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.url.URLHandler;
import org.broadleafcommerce.cms.url.URlHandlerDaoImpl;
import org.springframework.web.filter.OncePerRequestFilter;

public class URLHandlerFilter extends OncePerRequestFilter {
	@Resource
	private URlHandlerDaoImpl urlHandlerDao;

	protected void doFilterInternal(HttpServletRequest req,
			HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {

		
		ServletContext servletContext = req.getSession().getServletContext();
		RequestDispatcher rd = servletContext.getNamedDispatcher("default");
		if (rd == null) {
			throw new IllegalStateException(
					"A RequestDispatcher could not be located for the default servlet");
		}
		URLHandler url = urlHandlerDao.findPageByURI(req.getServletPath());
		LogFactory.getLog(this.getClass()).debug("new request for "+req.getServletPath()+this);
		if (url != null && req.getServletPath().equals(url.getIncomingURL())) {
			
			if (url.getUrlRedirectType()==url.getUrlRedirectType().FORWARD) {
				RequestDispatcher rd2 = req.getRequestDispatcher(url.getNewURL());
				rd2.forward(req, res);
			} else if (url.getUrlRedirectType()==url.getUrlRedirectType().REDIRECT_PERM) {
				res.setStatus(res.SC_MOVED_PERMANENTLY); // error code 301
				res.setHeader("Location", url.getNewURL());
				// res.sendRedirect("welcome.htm");//move temporarily error code
				// 302
			}else if (url.getUrlRedirectType()==url.getUrlRedirectType().REDIRECT_TEMP) {
			    res.sendRedirect( url.getNewURL());//move temporarily error code
				// 302
			}
		} else
			chain.doFilter(req, res);
	}
}
