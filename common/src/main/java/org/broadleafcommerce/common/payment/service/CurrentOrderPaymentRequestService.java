/*
 * #%L
 * BroadleafCommerce Common Libraries
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
/**
 * 
 */
package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;


/**
 * Simple interface for returning a {@link PaymentRequestDTO} based on the current order in the system (like something on
 * threadlocal).
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface CurrentOrderPaymentRequestService {

    /**
     * Returns a {@link PaymentRequestDTO} based on all the information from the current order in the system, like one
     * on threadlocal
     */
    public PaymentRequestDTO getPaymentRequestFromCurrentOrder();

    /**
     * adds a concept of an "order attribute" to the current order in the system
     * @param orderAttributeKey
     * @param orderAttributeValue
     * @throws PaymentException
     */
    public void addOrderAttributeToCurrentOrder(String orderAttributeKey, String orderAttributeValue) throws PaymentException;

    /**
     * adds a concept of an "order attribute" to an order in the system based on ID.
     * @param orderAttributeKey
     * @param orderAttributeValue
     * @throws PaymentException
     */
    public void addOrderAttributeToOrder(Long orderId, String orderAttributeKey, String orderAttributeValue) throws PaymentException;

    /**
     * retrieve an "order attribute" value on the current order in the system
     * @param orderAttributeKey
     * @return
     */
    public String retrieveOrderAttributeFromCurrentOrder(String orderAttributeKey);

    /**
     * retrieve an "order attribute" value based on the order id in the system
     * @param orderId
     * @param orderAttributeKey
     * @return
     */
    public String retrieveOrderAttributeFromOrder(Long orderId, String orderAttributeKey);

}
