package org.broadleafcommerce.security;

import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.profile.domain.Customer;
import org.springframework.security.context.SecurityContextHolder;

public class CustomerState {

    private final static String CUSTOMER_SESSION_ATTR_NAME = "broadleaf_commerce.customer";

    public static Customer getCustomer(HttpServletRequest request) {
        return (Customer) request.getSession().getAttribute(CUSTOMER_SESSION_ATTR_NAME);
    }

    public static void setCustomer(Customer customer, HttpServletRequest request) {
        request.getSession().setAttribute(CUSTOMER_SESSION_ATTR_NAME, customer);
    }

    public static Long getCustomerId(HttpServletRequest request) {
        return getCustomer(request) == null ? null : getCustomer(request).getId();
    }

    public static boolean isCustomerAuthenticated() {
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
        }
        return false;
    }

}
