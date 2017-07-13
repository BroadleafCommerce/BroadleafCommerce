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
        customerSavedPaymentDao.save(savedPayment);
    }

    @Override
    public List<SavedPayment> readSavedPaymentsByCustomerId(Long customerId) {
        return customerSavedPaymentDao.readSavedPaymentsByCustomerId(customerId);
    }

    @Override
    @Transactional("blTransactionManager")
    public void deleteSavedPayment(SavedPayment savedPayment) {
        customerSavedPaymentDao.deleteSavedPayment(savedPayment);
    }

    @Override
    @Transactional("blTransactionManager")
    public SavedPayment create() {
        return customerSavedPaymentDao.create();
    }
}
