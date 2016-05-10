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
import org.broadleafcommerce.profile.core.domain.CustomerPhone;
import org.broadleafcommerce.profile.core.domain.CustomerPhoneImpl;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository("blCustomerPhoneDao")
public class CustomerPhoneDaoImpl implements CustomerPhoneDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @SuppressWarnings("unchecked")
    public List<CustomerPhone> readActiveCustomerPhonesByCustomerId(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_ACTIVE_CUSTOMER_PHONES_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    public CustomerPhone save(CustomerPhone customerPhone) {
        return em.merge(customerPhone);
    }

    public CustomerPhone readCustomerPhoneById(Long customerPhoneId) {
        return (CustomerPhone) em.find(CustomerPhoneImpl.class, customerPhoneId);
    }

    public void makeCustomerPhoneDefault(Long customerPhoneId, Long customerId) {
        List<CustomerPhone> customerPhones = readActiveCustomerPhonesByCustomerId(customerId);
        for (CustomerPhone customerPhone : customerPhones) {
            customerPhone.getPhone().setDefault(customerPhone.getId().equals(customerPhoneId));
            em.merge(customerPhone);
        }
    }

    public void deleteCustomerPhoneById(Long customerPhoneId) {
        CustomerPhone customerPhone = readCustomerPhoneById(customerPhoneId);
        if (customerPhone != null) {
            em.remove(customerPhone);
        }
    }

    @SuppressWarnings("unchecked")
    public CustomerPhone findDefaultCustomerPhone(Long customerId) {
        Query query = em.createNamedQuery("BC_FIND_DEFAULT_PHONE_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        List<CustomerPhone> customerPhones = query.getResultList();
        return customerPhones.isEmpty() ? null : customerPhones.get(0);
    }

    @SuppressWarnings("unchecked")
    public List<CustomerPhone> readAllCustomerPhonesByCustomerId(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_ALL_CUSTOMER_PHONES_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    public CustomerPhone create() {
        return (CustomerPhone) entityConfiguration.createEntityInstance(CustomerPhone.class.getName());
    }
}
