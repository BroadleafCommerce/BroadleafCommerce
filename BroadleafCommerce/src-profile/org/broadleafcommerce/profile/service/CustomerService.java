package org.broadleafcommerce.profile.service;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.util.PasswordChange;

public interface CustomerService {

    public Customer saveCustomer(Customer customer);

    public Customer readCustomerByUsername(String customerName);

    public Customer readCustomerByEmail(String emailAddress);

    public Customer changePassword(PasswordChange passwordChange);

    public Customer readCustomerById(Long userId);
}
