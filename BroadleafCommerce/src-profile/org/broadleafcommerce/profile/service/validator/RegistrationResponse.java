package org.broadleafcommerce.profile.service.validator;

import org.broadleafcommerce.profile.domain.Customer;

public class RegistrationResponse extends BaseResponse {

    private Customer customer;

    public RegistrationResponse(Object target, String objectName) {
        super(target, objectName);
        this.customer = (Customer) target;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
