/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.profile.dao;

import java.util.Date;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("blCustomerDao")
public class CustomerDaoImpl implements CustomerDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource
    protected EntityConfiguration entityConfiguration;

    @SuppressWarnings("unchecked")
    public Customer readCustomerById(Long id) {
        return (Customer) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.Customer"), id);
    }

    public Customer readCustomerByUsername(String username) {
        Query query = em.createNamedQuery("BC_READ_CUSTOMER_BY_USER_NAME");
        query.setParameter("username", username);
        try {
            return (Customer) query.getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }

    public Customer readCustomerByEmail(String emailAddress) {
        Query query = em.createNamedQuery("BC_READ_CUSTOMER_BY_EMAIL");
        query.setParameter("email", emailAddress);
        try {
            return (Customer) query.getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }

    public Customer save(Customer customer) {
        if (customer.getAuditable() != null) {
            customer.getAuditable().setDateUpdated(new Date());
        }
        if (customer.getId() == null) {
            em.persist(customer);
        } else {
            customer = em.merge(customer);
        }
        return customer;
    }

    public Customer create() {
        Customer customer =  (Customer) entityConfiguration.createEntityInstance(Customer.class.getName());
        customer.getAuditable().setDateCreated(new Date());
        return customer;
    }

}
