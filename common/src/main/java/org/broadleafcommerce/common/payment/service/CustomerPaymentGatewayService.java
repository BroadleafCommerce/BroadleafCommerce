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
package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.web.payment.controller.CustomerPaymentGatewayAbstractController;

/**
 * <p>The default implementation of this interface is represented in the core Broadleaf framework at
 * {@link org.broadleafcommerce.core.payment.service.DefaultCustomerPaymentGatewayService}. This is designed as
 * a generic contract for allowing payment modules to tokenize payments and add it to a customer profile
 * represented in Broadleaf while still staying decoupled from any of the Broadleaf core framework concepts.</p>
 *
 * <p>These service methods are usually invoked from the controller that listens to the endpoint hit by the external payment
 * provider (which should be a subclass of {@link org.broadleafcommerce.common.web.payment.controller.CustomerPaymentGatewayAbstractController}).</p>
 *
 * @see {@link CustomerPaymentGatewayAbstractController}
 * @author Elbert Bautista (elbertbautista)
 */
public interface CustomerPaymentGatewayService {

    /**
     * @param responseDTO the response from the gateway
     * @param config configuration values for the payment gateway
     * @return a unique ID of the customer payment token as it is saved in the core commerce engine. If using Broadleaf's
     * core commerce engine, it will be the ID of the created {@link CustomerPayment} entity.
     * @throws IllegalArgumentException
     */
    Long createCustomerPaymentFromResponseDTO(PaymentResponseDTO responseDTO, PaymentGatewayConfiguration config)
            throws IllegalArgumentException;

    /**
     * @param responseDTO the response from the gateway
     * @param config configuration values for the payment gateway
     * @return a unique ID of the customer payment token as it is saved in the core commerce engine. If using Broadleaf's
     * core commerce engine, it will be the ID of the created {@link CustomerPayment} entity.
     * @throws IllegalArgumentException
     */
    Long updateCustomerPaymentFromResponseDTO(PaymentResponseDTO responseDTO, PaymentGatewayConfiguration config)
            throws IllegalArgumentException;

    /**
     * @param responseDTO the response from the gateway
     * @param config configuration values for the payment gateway
     *
     * @throws IllegalArgumentException
     */
    void deleteCustomerPaymentFromResponseDTO(PaymentResponseDTO responseDTO, PaymentGatewayConfiguration config)
            throws IllegalArgumentException;

}
