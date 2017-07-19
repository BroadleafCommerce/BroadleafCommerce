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
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.domain.CustomerPaymentImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository("blCustomerPaymentDao")
public class CustomerPaymentDaoImpl implements CustomerPaymentDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public List<CustomerPayment> readCustomerPaymentsByCustomerId(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_CUSTOMER_PAYMENTS_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    @Override
    public CustomerPayment save(CustomerPayment customerPayment) {
        return em.merge(customerPayment);
    }

    @Override
    public CustomerPayment readCustomerPaymentByToken(String token) {
        Query query = em.createNamedQuery("BC_READ_CUSTOMER_PAYMENT_BY_TOKEN");
        query.setParameter("token", token);
        CustomerPayment payment = null;
        try{
            payment = (CustomerPayment) query.getSingleResult();
        } catch (NoResultException e) {
           //do nothing
        }
        return  payment;
    }

    @Override
    public CustomerPayment readCustomerPaymentById(Long customerPaymentId) {
        return (CustomerPayment) em.find(CustomerPaymentImpl.class, customerPaymentId);
    }

    @Override
    public void deleteCustomerPaymentById(Long customerPaymentId) {
        CustomerPayment customerPayment = readCustomerPaymentById(customerPaymentId);
        if (customerPayment != null) {
            em.remove(customerPayment);
        }
    }

    @Override
    public void deleteCustomerPaymentByToken(String token) {
        CustomerPayment customerPayment = readCustomerPaymentByToken(token);
        if (customerPayment != null) {
            em.remove(customerPayment);
        }
    }

    @Override
    public CustomerPayment create() {
        return (CustomerPayment) entityConfiguration.createEntityInstance(CustomerPayment.class.getName());
    }

}
