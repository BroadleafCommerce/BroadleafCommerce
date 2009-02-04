package org.broadleafcommerce.security;

import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.profile.domain.Customer;

public class CustomerState {

    public static Customer getCustomer(HttpServletRequest request) {
        return (Customer) request.getSession().getAttribute("broadleaf_commerce.customer");
    }

    public static void setCustomer(Customer customer, HttpServletRequest request) {
        request.getSession().setAttribute("broadleaf_commerce.customer", customer);
    }
}
