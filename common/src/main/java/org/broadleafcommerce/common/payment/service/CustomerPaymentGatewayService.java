/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.payment.service;

import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;

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
    public Long createCustomerPaymentFromResponseDTO(PaymentResponseDTO responseDTO, PaymentGatewayConfiguration config)
            throws IllegalArgumentException;

}
