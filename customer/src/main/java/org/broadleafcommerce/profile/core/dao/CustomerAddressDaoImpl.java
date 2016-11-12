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

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.CustomerAddressImpl;
import org.springframework.stereotype.Repository;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository("blCustomerAddressDao")
public class CustomerAddressDaoImpl implements CustomerAddressDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @SuppressWarnings("unchecked")
    public List<CustomerAddress> readActiveCustomerAddressesByCustomerId(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_ACTIVE_CUSTOMER_ADDRESSES_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        query.setParameter("archived", 'N');
        return query.getResultList();
    }

    public CustomerAddress save(CustomerAddress customerAddress) {
            return em.merge(customerAddress);
    }

    public CustomerAddress create() {
        return (CustomerAddress) entityConfiguration.createEntityInstance(CustomerAddress.class.getName());
    }

    public CustomerAddress readCustomerAddressById(Long customerAddressId) {
        return (CustomerAddress) em.find(CustomerAddressImpl.class, customerAddressId);
    }

    public void makeCustomerAddressDefault(Long customerAddressId, Long customerId) {
        List<CustomerAddress> customerAddresses = readActiveCustomerAddressesByCustomerId(customerId);
        for (CustomerAddress customerAddress : customerAddresses) {
            customerAddress.getAddress().setDefault(customerAddress.getId().equals(customerAddressId));
            em.merge(customerAddress);
        }
    }

    public void deleteCustomerAddressById(Long customerAddressId) {
        CustomerAddress customerAddress = readCustomerAddressById(customerAddressId);
        if (customerAddress != null) {
            em.remove(customerAddress);
        }
    }

    @SuppressWarnings("unchecked")
    public CustomerAddress findDefaultCustomerAddress(Long customerId) {
        Query query = em.createNamedQuery("BC_FIND_DEFAULT_ADDRESS_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        List<CustomerAddress> customerAddresses = query.getResultList();
        return customerAddresses.isEmpty() ? null : customerAddresses.get(0);
    }
}
