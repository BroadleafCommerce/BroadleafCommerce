package org.broadleafcommerce.security;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.springframework.security.Authentication;
import org.springframework.stereotype.Service;

@Service("customerStateStore")
public class CustomerStateStore implements PostLoginObserver {

    @Resource
    private CustomerService customerService;

    public void processPostLogin(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        Customer customer = customerService.readCustomerByUsername((String) authResult.getPrincipal());
        CookieUtils.setCookieValue(response, CookieUtils.CUSTOMER_COOKIE_NAME, customer.getId() + "");
        CustomerState.setCustomer(customer, request);
    }
}
