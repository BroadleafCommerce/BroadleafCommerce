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
package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.dao.CustomerSavedPaymentDao;
import org.broadleafcommerce.profile.core.domain.SavedPayment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jacob Mitash
 */
@Service("blCustomerSavedPaymentService")
public class CustomerSavedPaymentServiceImpl implements CustomerSavedPaymentService {

    @Resource(name = "blCustomerSavedPaymentDao")
    protected CustomerSavedPaymentDao customerSavedPaymentDao;

    @Override
    @Transactional("blTransactionManager")
    public void saveSavedPayment(SavedPayment savedPayment) {

        //Make sure all other payments are non-default if this one is
        if(savedPayment.isDefaultMethod()) {
            List<SavedPayment> savedPayments = readSavedPaymentsByCustomerId(savedPayment.getCustomer().getId());

            for (SavedPayment payment : savedPayments) {
                if (payment.isDefaultMethod() && payment != savedPayment) {
                    payment.setDefaultMethod(false);
                    saveSavedPayment(payment);
                }
            }
        }

        customerSavedPaymentDao.save(savedPayment);
    }

    @Override
    public List<SavedPayment> readSavedPaymentsByCustomerId(Long customerId) {
        return customerSavedPaymentDao.readSavedPaymentsByCustomerId(customerId);
    }

    @Override
    @Transactional
    public void makeDefaultSavedPayment(Long savedPaymentId) {
        SavedPayment payment = customerSavedPaymentDao.readSavedPaymentById(savedPaymentId);
        payment.setDefaultMethod(true);
        saveSavedPayment(payment);
    }

    @Override
    @Transactional("blTransactionManager")
    public void deleteSavedPayment(Long savedPaymentId) {
        SavedPayment savedPayment = customerSavedPaymentDao.readSavedPaymentById(savedPaymentId);

        if(savedPayment == null) {
            return;
        }

        //Set new default if able
        List<SavedPayment> savedPayments = readSavedPaymentsByCustomerId(savedPayment.getCustomer().getId());
        if(savedPayments != null && savedPayments.size() > 1) {
            SavedPayment futureDefault = savedPayments.get(1);
            futureDefault.setDefaultMethod(true);
            saveSavedPayment(futureDefault);
        }

        customerSavedPaymentDao.deleteSavedPayment(savedPayment.getId());
    }

    @Override
    @Transactional("blTransactionManager")
    public SavedPayment create(Long customerId) {
        SavedPayment savedPayment = customerSavedPaymentDao.create();

        //Make default if only payment
        savedPayment.setDefaultMethod(!hasPaymentMethods(customerId));

        return savedPayment;
    }

    @Override
    public boolean hasPaymentMethods(Long customerId) {
        List<SavedPayment> savedPayments = readSavedPaymentsByCustomerId(customerId);

        return savedPayments != null && savedPayments.size() > 0;
    }
}
