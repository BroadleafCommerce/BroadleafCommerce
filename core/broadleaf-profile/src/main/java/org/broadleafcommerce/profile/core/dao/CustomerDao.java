/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.profile.core.dao;

import org.broadleafcommerce.profile.core.domain.Customer;

import java.util.List;

public interface CustomerDao {

    public Customer readCustomerById(Long id);

    /**
     * Returns the first customer that match the passed in username.
     * 
     * @param username
     * @return
     */
    public Customer readCustomerByUsername(String username);
    
    /**
     * Returns all customers that match the passed in username.
     * 
     * @param username
     * @return
     */
    public List<Customer> readCustomersByUsername(String username);

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

}
