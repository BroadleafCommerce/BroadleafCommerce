package org.broadleafcommerce.profile.web.form;

import java.io.Serializable;

import org.broadleafcommerce.profile.domain.Customer;

public class RegisterCustomerForm implements Serializable {
    private static final long serialVersionUID = 1L;

    private Customer customer;
    private String password;
    private String passwordConfirm;

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
    public String getPasswordConfirm() {
        return passwordConfirm;
    }
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
