package org.broadleafcommerce.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class CurrentCustomerInterceptor extends HandlerInterceptorAdapter {

	private final static String CUSTOMER_SESSION_ATTR_NAME = CustomerState.getCustomerSessionAttributeName();

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Customer sessionCustomer = (Customer) request.getSession().getAttribute(CUSTOMER_SESSION_ATTR_NAME);
		if (sessionCustomer != null) {
			return true;
		}
		String cookieCustomerIdVal = CookieUtils.getCookieValue(request, CookieUtils.CUSTOMER_COOKIE_NAME);
		Long cookieCustomerId = null;
		if (cookieCustomerIdVal != null) {
			cookieCustomerId = new Long(cookieCustomerIdVal);
		}
		WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(request
				.getSession().getServletContext());
		CustomerService customerService = (CustomerService) applicationContext.getBean("customerService");
		if (cookieCustomerId != null) {
			Customer persistedCookieCustomer = customerService.readCustomerById(cookieCustomerId);
			if (persistedCookieCustomer != null) {
				request.getSession().setAttribute(CUSTOMER_SESSION_ATTR_NAME, persistedCookieCustomer);
				return true;
			} else {
				Customer anonymousCookieCustomer = customerService.createCustomerFromId(cookieCustomerId);
				request.getSession().setAttribute(CUSTOMER_SESSION_ATTR_NAME, anonymousCookieCustomer);
				return true;
			}
		}

		// if no customer in session or cookie, create a new one
		Customer firstTimeCustomer = customerService.createCustomerFromId(null);
		CookieUtils.setCookieValue(response, CookieUtils.CUSTOMER_COOKIE_NAME, firstTimeCustomer.getId() + "");
		request.getSession().setAttribute(CUSTOMER_SESSION_ATTR_NAME, firstTimeCustomer);
		return true;
	}
}
