/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
import org.broadleafcommerce.common.web.payment.controller.PaymentGatewayAbstractController;

/**
 * <p>The default implementation of this interface is represented in the core Broadleaf framework at
 * {@link org.broadleafcommerce.core.payment.service.BroadleafPaymentGatewayCheckoutService}. This is designed as
 * a generic contract for allowing payment modules to add payments to an order represented in Broadleaf while still
 * staying decoupled from any of the Broadleaf core framework concepts.</p>
 * 
 * <p>These service methods are usually invoked from the controller that listens to the endpoint hit by the external payment
 * provider (which should be a subclass of {@link PaymentGatewayAbstractController}).</p>
 * 
 * @see {@link PaymentGatewayAbstractController}
 *
 * @author Elbert Bautista (elbertbautista)
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface PaymentGatewayCheckoutService {

    /**
     * @param responseDTO the response that came back from the gateway
     * @param configService configuration values for the payment gateway
     * @return a unique ID of the payment as it is saved in the Broadleaf domain. This ID can be referred to to retrieve
     * the payment on the Broadleaf side for other methods like {@link #markPaymentAsInvalid(Long)}
     * @throws IllegalArgumentException if the {@link PaymentResponseDTO#getValid()} returns false or if the order that
     * the {@link PaymentResponseDTO} is attempted to be applied to has already gone through checkout
     */
    public Long applyPaymentToOrder(PaymentResponseDTO responseDTO, PaymentGatewayConfigurationService configService)
        throws IllegalArgumentException;

    /**
     * Marks a given order payment as invalid. In the default implementation, this archives the payment. This can be
     * determined from the result of {@link #applyPaymentToOrder(PaymentResponseDTO, PaymentGatewayConfigurationService)}
     * @param orderPaymentId the payment ID to mark as invalid
     */
    public void markPaymentAsInvalid(Long orderPaymentId);

    /**
     * Initiates the checkout process for a given <b>orderId</b>. This is usually from {@link PaymentResponseDTO#getOrderId()}
     * @param orderId the order to check out
     * @return the response from checking out the order
     */
    public String initiateCheckout(Long orderId) throws Exception;

    /**
     * Looks up the order number for a particular order id from the {@link PaymentResponseDTO}. This can be used to redirect
     * the user coming from the payment gateway to the order confirmation page.
     * 
     * @param responseDTO the response from the gateway
     * @return The order number for order id. This method can return null if the order number has not already been set
     * (which usually means that the order has not already been checked out)
     * @throws IllegalArgumentException if the order cannot be found from the {@link PaymentResponseDTO}
     */
    public String lookupOrderNumberFromOrderId(PaymentResponseDTO responseDTO) throws IllegalArgumentException;

}
