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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.CustomerAddress;
import org.springframework.stereotype.Repository;

@Repository("blCustomerAddressDao")
public class CustomerAddressDaoJpa implements CustomerAddressDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName = "blPU")
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<CustomerAddress> readActiveCustomerAddressesByCustomerId(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_ACTIVE_CUSTOMER_ADDRESSES_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    public CustomerAddress maintainCustomerAddress(CustomerAddress customerAddress) {
        if (customerAddress.getId() == null) {
            em.persist(customerAddress);
        } else {
            customerAddress = em.merge(customerAddress);
        }
        return customerAddress;
    }

    @SuppressWarnings("unchecked")
    public CustomerAddress readCustomerAddressByIdAndCustomerId(Long customerAddressId, Long customerId) {
        Query query = em.createNamedQuery("BC_READ_CUSTOMER_ADDRESS_BY_ID_AND_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        query.setParameter("customerAddressId", customerAddressId);
        List<CustomerAddress> customerAddresses = query.getResultList();
        return customerAddresses.isEmpty() ? null : customerAddresses.get(0);
    }

    public void makeCustomerAddressDefault(Long customerAddressId, Long customerId) {
        List<CustomerAddress> customerAddresses = readActiveCustomerAddressesByCustomerId(customerId);
        for (CustomerAddress customerAddress : customerAddresses) {
            customerAddress.getAddress().setDefault(customerAddress.getId().equals(customerAddressId));
            em.merge(customerAddress);
        }
    }

    public void deleteCustomerAddressByIdAndCustomerId(Long customerAddressId, Long customerId) {
        // TODO: determine if hard delete or deactivate, and consider throwing exception if read fails
        CustomerAddress customerAddress = readCustomerAddressByIdAndCustomerId(customerAddressId, customerId);
        em.remove(customerAddress.getId());
    }

    @SuppressWarnings("unchecked")
    @Override
    public CustomerAddress findDefaultCustomerAddress(Long customerId) {
        Query query = em.createNamedQuery("BC_FIND_DEFAULT_ADDRESS_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        List<CustomerAddress> customerAddresses = query.getResultList();
        return customerAddresses.isEmpty() ? null : customerAddresses.get(0);
    }
}
