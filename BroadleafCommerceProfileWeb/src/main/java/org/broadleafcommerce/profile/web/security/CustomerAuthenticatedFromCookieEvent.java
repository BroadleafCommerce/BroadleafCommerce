package org.broadleafcommerce.profile.web.security;

import org.broadleafcommerce.profile.domain.Customer;
import org.springframework.context.ApplicationEvent;

public class CustomerAuthenticatedFromCookieEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;
    
    private Customer customer;

    public CustomerAuthenticatedFromCookieEvent(Customer customer, Object source) {
        super(source);
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
