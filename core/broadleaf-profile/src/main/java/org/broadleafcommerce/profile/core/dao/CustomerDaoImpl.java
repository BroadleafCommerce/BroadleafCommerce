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

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blCustomerDao")
public class CustomerDaoImpl implements CustomerDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public Customer readCustomerById(Long id) {
        return em.find(CustomerImpl.class, id);
    }

    public Customer readCustomerByUsername(String username) {        
        List<Customer> customers = readCustomersByUsername(username);
        return customers == null || customers.isEmpty() ? null : customers.get(0);
    }
   
    @SuppressWarnings("unchecked")
    public List<Customer> readCustomersByUsername(String username) {
        Query query = em.createNamedQuery("BC_READ_CUSTOMER_BY_USER_NAME");
        query.setParameter("username", username);        
        return query.getResultList();        
    }

    public Customer readCustomerByEmail(String emailAddress) {
        List<Customer> customers = readCustomersByEmail(emailAddress);
        return customers == null || customers.isEmpty() ? null : customers.get(0);
    }
    
    @SuppressWarnings("unchecked")
    public List<Customer> readCustomersByEmail(String emailAddress) {
        Query query = em.createNamedQuery("BC_READ_CUSTOMER_BY_EMAIL");
        query.setParameter("email", emailAddress);
        return query.getResultList();        
    }

    public Customer save(Customer customer) {
        return em.merge(customer);
    }

    public Customer create() {
        Customer customer =  (Customer) entityConfiguration.createEntityInstance(Customer.class.getName());
        return customer;
    }

}
