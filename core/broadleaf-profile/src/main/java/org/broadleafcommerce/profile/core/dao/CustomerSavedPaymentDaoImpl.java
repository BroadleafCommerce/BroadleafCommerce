/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
import org.broadleafcommerce.profile.core.domain.SavedPayment;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author Jacob Mitash
 */
@Repository("blCustomerSavedPaymentDao")
public class CustomerSavedPaymentDaoImpl implements CustomerSavedPaymentDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public void save(SavedPayment savedPayment) {
        em.merge(savedPayment);
    }

    @Override
    public List<SavedPayment> readSavedPaymentsByCustomerId(Long customerId) {
        TypedQuery<SavedPayment> query = em.createNamedQuery("BC_READ_SAVED_PAYMENTS", SavedPayment.class);
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    @Override
    public SavedPayment readSavedPaymentById(Long savedPaymentId) {
        TypedQuery<SavedPayment> query = em.createNamedQuery("BC_READ_SAVED_PAYMENT", SavedPayment.class);
        query.setParameter("savedPaymentId", savedPaymentId);
        return query.getSingleResult();
    }

    @Override
    public void deleteSavedPayment(Long savedPaymentId) {
        Query query = em.createNamedQuery("BC_REMOVE_SAVED_PAYMENT");
        query.setParameter("savedPaymentId", savedPaymentId);
        query.executeUpdate();
    }

    @Override
    public SavedPayment create() {
        return (SavedPayment) entityConfiguration.createEntityInstance(SavedPayment.class.getName());
    }
}
