/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;

/**
 * Simple interface for returning a {@link PaymentRequestDTO} based on the current customer in the system
 * (like something on threadlocal).
 **/
public interface CurrentCustomerPaymentRequestService {

    /**
     * Returns a {@link PaymentRequestDTO} based on all the information from the current customer in the system, like one
     * on threadlocal
     */
    public PaymentRequestDTO getPaymentRequestFromCurrentCustomer();

    /**
     * adds a concept of an "customer attribute" to the current customer in the system
     * @param customerAttributeKey
     * @param customerAttributeValue
     * @throws PaymentException
     */
    public void addCustomerAttributeToCurrentCustomer(String customerAttributeKey, String customerAttributeValue) throws PaymentException;

    /**
     * adds a concept of a "customer attribute" to a customer in the system based on ID.
     * @param customerId
     * @param customerAttributeKey
     * @param customerAttributeValue
     * @throws PaymentException
     */
    public void addCustomerAttributeToCustomer(Long customerId, String customerAttributeKey, String customerAttributeValue) throws PaymentException;

    /**
     * retrieve a "customer attribute" value on the current customer in the system
     * @param customerAttributeKey
     * @return
     */
    public String retrieveCustomerAttributeFromCurrentCustomer(String customerAttributeKey);

    /**
     * retrieve a "customer attribute" value based on the customer id in the system
     * @param customerId
     * @param customerAttributeKey
     * @return
     */
    public String retrieveCustomerAttributeFromCustomer(Long customerId, String customerAttributeKey);
}
