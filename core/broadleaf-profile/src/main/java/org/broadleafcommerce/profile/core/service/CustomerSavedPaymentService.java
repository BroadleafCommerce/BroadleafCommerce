package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.domain.SavedPaymentImpl;

import java.util.List;

/**
 * @author Jacob Mitash
 */
public interface CustomerSavedPaymentService {

    void saveSavedPayment(SavedPaymentImpl savedPaymentDTO);

    List<SavedPaymentImpl> readSavedPaymentsByCustomer(Long customerId);

    SavedPaymentImpl readSavedPaymentById(Long savedPaymentId);

    void deleteSavedPayment(Long savedPaymentId);

    SavedPaymentImpl findDefaultSavedPayment(Long customerId);

    void create();
}
