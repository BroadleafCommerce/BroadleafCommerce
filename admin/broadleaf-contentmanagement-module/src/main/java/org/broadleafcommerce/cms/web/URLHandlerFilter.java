package org.broadleafcommerce.cms.web;

import org.broadleafcommerce.cms.url.domain.URLHandler;
import org.broadleafcommerce.cms.url.service.URLHandlerService;
import org.broadleafcommerce.cms.url.type.URLRedirectType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gwt.http.client.Response;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Responsible for setting up the site and locale used by Broadleaf Commerce components.
 *
 * @author bpolster
 */
@Component("blURLHandlerFilter")
public class URLHandlerFilter extends OncePerRequestFilter {
	
    @Resource(name = "blURLHandlerService")
    private URLHandlerService urlHandlerService;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String contextPath = request.getContextPath();
		String requestURIWithoutContext;
		if (request.getContextPath() != null) {
			requestURIWithoutContext = request.getRequestURI().substring(request.getContextPath().length());
		} else {
			requestURIWithoutContext = request.getRequestURI();
		}
		URLHandler handler = urlHandlerService.findURLHandlerByURI(requestURIWithoutContext);
		
		if (handler != null) {
			if (URLRedirectType.FORWARD == handler.getUrlRedirectType()) {				
				request.getRequestDispatcher(handler.getNewURL()).forward(request, response);				
			} else if (URLRedirectType.REDIRECT_PERM == handler.getUrlRedirectType()) {
				String url = fixRedirectUrl(contextPath, handler.getNewURL());
				response.setStatus(Response.SC_MOVED_PERMANENTLY);
				response.setHeader( "Location", url);
				response.setHeader( "Connection", "close" );
			} else if (URLRedirectType.REDIRECT_TEMP == handler.getUrlRedirectType()) {
				String url = fixRedirectUrl(contextPath, handler.getNewURL());
				response.sendRedirect(url);				
			}			
		} else {
	        filterChain.doFilter(request, response);
		}
	}
	
	/**
	 * If the url does not include "//" then the system will ensure that the application context
	 * is added to the start of the URL.
	 * 
	 * @param url
	 * @return
	 */
	protected String fixRedirectUrl(String contextPath, String url) {
		if (url.indexOf("//")  < 0) {

			if (contextPath != null && (! "".equals(contextPath))) {
				if (! url.startsWith("/")) {
					url = "/" + url;
				}
				if (! url.startsWith(contextPath)) {
					url = contextPath + url;
				}
			}			
		}
		return url;
		
	}
}
