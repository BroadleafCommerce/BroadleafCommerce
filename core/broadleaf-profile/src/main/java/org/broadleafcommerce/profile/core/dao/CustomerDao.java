/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.profile.core.domain.Customer;

public interface CustomerDao {

    public Customer readCustomerById(Long id);

    public Customer readCustomerByExternalId(String id);

    public List<Customer> readCustomersByIds(List<Long> ids);

    /**
     * Reads a batch list of customers from the DB.
     * @param start
     * @param pageSize
     * @return
     */
    public List<Customer> readBatchCustomers(int start, int pageSize);

    /**
     * Returns the first customer that match the passed in username, with caching defaulted.
     *
     * @param username
     * @return
     */
    public Customer readCustomerByUsername(String username);

    /**
     * Returns the first customer that match the passed in username, and caches according to
     * cacheable.
     *
     * @param username
     * @param cacheable
     * @return
     */
    public Customer readCustomerByUsername(String username, Boolean cacheable);

    /**
     * Returns all customers that match the passed in username, with caching defaulted.
     *
     * @param username
     * @return
     */
    public List<Customer> readCustomersByUsername(String username);

    /**
     * Returns all customers that match the passed in username, and caches according to
     * cacheable.
     *
     * @param username
     * @param cacheable
     * @return
     */
    public List<Customer> readCustomersByUsername(String username, Boolean cacheable);

    public Customer save(Customer customer);

    /**
     * Returns the first customer that matches the passed in email.
     *
     * @param emailAddress
     * @return
     */
    public Customer readCustomerByEmail(String emailAddress);

    /**
     * Returns all customers that matches the passed in email.
     *
     * @param emailAddress
     * @return
     */
    public List<Customer> readCustomersByEmail(String emailAddress);

    public Customer create();

    /**
     * Remove a customer from the persistent store
     *
     * @param customer the customer entity to remove
     */
    void delete(Customer customer);

    Long readNumberOfCustomers();
}
