package org.broadleafcommerce.profile.service;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.listener.PostRegistrationObserver;
import org.broadleafcommerce.profile.service.validator.RegistrationResponse;
import org.broadleafcommerce.profile.util.PasswordChange;

public interface CustomerService {

    public Customer saveCustomer(Customer customer);

    public RegistrationResponse registerCustomer(Customer customer, String password, String passwordConfirm);

    public Customer readCustomerByUsername(String customerName);

    public Customer readCustomerByEmail(String emailAddress);

    public Customer changePassword(PasswordChange passwordChange);

    public Customer readCustomerById(Long userId);

    /**
     * Returns a <code>Customer</code> by first looking in the database, otherwise creating a new non-persisted <code>Customer</code>
     * @param customerId the id of the customer to lookup
     * @return either a <code>Customer</code> from the database if it exists, or a new non-persisted <code>Customer</code>
     */
    public Customer createCustomerFromId(Long customerId);

    public void addListener(PostRegistrationObserver postRegisterListeners);

    public void removeListener(PostRegistrationObserver postRegisterListeners);

    public void notifyListeners(Customer customer);
}
