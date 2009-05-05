package org.broadleafcommerce.profile.dao;

import org.broadleafcommerce.profile.domain.Customer;

public interface CustomerDao {

    public Customer readCustomerById(Long id);

    public Customer readCustomerByUsername(String username);

    public Customer save(Customer customer);

    public Customer readCustomerByEmail(String emailAddress);
}
