package org.broadleafcommerce.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.springframework.security.Authentication;

public class CustomerStateStore implements PostLoginObserver {

    private CustomerService customerService;

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
        Customer customer = customerService.readCustomerByUsername((String) authResult.getPrincipal());
        CustomerState.setCustomer(customer, request);
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }
}
