package org.broadleafcommerce.profile.service.validator;

import org.broadleafcommerce.profile.domain.Customer;

public class RegistrationRequest {

    private Customer customer;

    private String password;

    private String paswordConfirm;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPaswordConfirm() {
        return paswordConfirm;
    }

    public void setPaswordConfirm(String paswordConfirm) {
        this.paswordConfirm = paswordConfirm;
    }
}
