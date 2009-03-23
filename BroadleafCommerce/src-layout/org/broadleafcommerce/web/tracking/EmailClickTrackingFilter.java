package org.broadleafcommerce.web.tracking;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.email.service.EmailTrackingManager;

/**
 * 
 * @author jfischer
 *
 */
public class EmailClickTrackingFilter implements Filter {
	
	private EmailTrackingManager emailTrackingManager;

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		//do nothing
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String emailId = request.getParameter("email_id");
        if ( emailId != null ) {
        	emailTrackingManager.recordClick( Long.valueOf(emailId) , (HttpServletRequest) request);
        }
        chain.doFilter(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		//do nothing
	}

	/**
	 * @return the emailTrackingManager
	 */
	public EmailTrackingManager getEmailTrackingManager() {
		return emailTrackingManager;
	}

	/**
	 * @param emailTrackingManager the emailTrackingManager to set
	 */
	public void setEmailTrackingManager(EmailTrackingManager emailTrackingManager) {
		this.emailTrackingManager = emailTrackingManager;
	}

}
