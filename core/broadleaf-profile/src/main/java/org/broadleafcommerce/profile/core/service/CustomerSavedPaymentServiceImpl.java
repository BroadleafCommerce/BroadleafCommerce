package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.dao.CustomerSavedPaymentDao;
import org.broadleafcommerce.profile.core.domain.SavedPayment;

import java.util.List;

/**
 * @author Jacob Mitash
 */
public class CustomerSavedPaymentServiceImpl implements CustomerSavedPaymentService {

    protected CustomerSavedPaymentDao customerSavedPaymentDao;

    @Override
    public void saveSavedPayment(SavedPayment savedPayment) {
        customerSavedPaymentDao.save(savedPayment);
    }

    @Override
    public List<SavedPayment> readSavedPaymentsByCustomerId(Long customerId) {
        return customerSavedPaymentDao.readSavedPaymentsByCustomerId(customerId);
    }

    @Override
    public void deleteSavedPayment(SavedPayment savedPayment) {
        customerSavedPaymentDao.deleteSavedPayment(savedPayment);
    }

    @Override
    public SavedPayment create() {
        return customerSavedPaymentDao.create();
    }
}
