package org.broadleafcommerce.profile.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.profile.web.security.PostLoginObserver;
import org.broadleafcommerce.profile.web.security.PreLogoutObserver;
import org.springframework.security.Authentication;
import org.springframework.stereotype.Component;

@Component("customerStateStore")
public class CustomerStateStore implements PostLoginObserver, PreLogoutObserver {

    @Resource
    private CustomerService customerService;

    @Resource
    private CustomerState customerState;

    public void processPostLogin(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        Customer customer = customerService.readCustomerByUsername((String) authResult.getPrincipal());
        customer.setAuthenticated(true);
        CookieUtils.setCookieValue(response, CookieUtils.CUSTOMER_COOKIE_NAME, customer.getId() + "","/",604800);
        customerState.setCustomer(customer, request);
    }

    @Override
    public void processPreLogout(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        CookieUtils.invalidateCookie(response, CookieUtils.CUSTOMER_COOKIE_NAME);
    }
}
