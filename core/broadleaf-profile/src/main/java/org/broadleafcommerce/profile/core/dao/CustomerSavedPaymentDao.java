package org.broadleafcommerce.profile.core.dao;

import org.broadleafcommerce.profile.core.domain.SavedPayment;

import java.util.List;

/**
 * @author Jacob Mitash
 */
public interface CustomerSavedPaymentDao {

    void save(SavedPayment savedPayment);

    List<SavedPayment> readSavedPaymentsByCustomerId(Long customerId);

    void deleteSavedPayment(SavedPayment savedPayment);

    SavedPayment create();
}
