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
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository("blCustomerDao")
public class CustomerDaoImpl implements CustomerDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public Customer readCustomerById(Long id) {
        return em.find(CustomerImpl.class, id);
    }

    @Override
    public Customer readCustomerByUsername(String username) {
        return readCustomerByUsername(username, true);
    }

    @Override
    public Customer readCustomerByUsername(String username, Boolean cacheable) {
        List<Customer> customers = readCustomersByUsername(username, cacheable);
        return customers == null || customers.isEmpty() ? null : customers.get(0);
    }

    @Override
    public List<Customer> readCustomersByUsername(String username) {
        return readCustomersByUsername(username, true);
    }

    @Override
    public List<Customer> readCustomersByUsername(String username, Boolean cacheable) {
        TypedQuery<Customer> query = em.createNamedQuery("BC_READ_CUSTOMER_BY_USER_NAME", Customer.class);
        query.setParameter("username", username);
        query.setHint(QueryHints.HINT_CACHEABLE, cacheable);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Order");
        return query.getResultList();
    }

    @Override
    public Customer readCustomerByEmail(String emailAddress) {
        List<Customer> customers = readCustomersByEmail(emailAddress);
        return customers == null || customers.isEmpty() ? null : customers.get(0);
    }
    
    @Override
    public List<Customer> readCustomersByEmail(String emailAddress) {
        TypedQuery<Customer> query = em.createNamedQuery("BC_READ_CUSTOMER_BY_EMAIL", Customer.class);
        query.setParameter("email", emailAddress);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setHint(QueryHints.HINT_CACHE_REGION, "query.Order");
        return query.getResultList();        
    }

    @Override
    public Customer save(Customer customer) {
        return em.merge(customer);
    }

    @Override
    public Customer create() {
        Customer customer =  (Customer) entityConfiguration.createEntityInstance(Customer.class.getName());
        return customer;
    }

    @Override
    public void delete(Customer customer) {
        if (!em.contains(customer)) {
            customer = readCustomerById(customer.getId());
        }
        em.remove(customer);
    }
}
