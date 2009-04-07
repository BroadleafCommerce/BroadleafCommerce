package org.broadleafcommerce.security;

import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.profile.domain.Customer;

public class CustomerState {

	private static String sessionAttributeName = "customer";

    public static Customer getCustomer(HttpServletRequest request) {
    	return (Customer) request.getSession().getAttribute(sessionAttributeName);
    }

    public static void setCustomer(Customer customer, HttpServletRequest request) {
        request.getSession().setAttribute(sessionAttributeName, customer);
    }

    public static Long getCustomerId(HttpServletRequest request) {
        return getCustomer(request) == null ? null : getCustomer(request).getId();
    }

    public static void setCustomerSessionAttributeName(String sessionAttributeName) {
    	CustomerState.sessionAttributeName = sessionAttributeName;
    }

    public static String getCustomerSessionAttributeName() {
    	return sessionAttributeName;
    }

}
