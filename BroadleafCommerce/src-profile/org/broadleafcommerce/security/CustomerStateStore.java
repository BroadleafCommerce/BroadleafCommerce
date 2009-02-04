package org.broadleafcommerce.security;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.UserService;
import org.springframework.security.Authentication;
import org.springframework.security.userdetails.User;

public class CustomerStateStore implements PostLoginObserver {

    @Resource(name = "userService")
    private UserService userService;

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        Customer customer = userService.readCustomerByUsername(((User) authResult.getPrincipal()).getUsername());
        CustomerState.setCustomer(customer, request);
    }
}
