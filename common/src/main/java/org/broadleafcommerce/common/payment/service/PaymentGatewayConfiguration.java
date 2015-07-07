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

import org.broadleafcommerce.common.payment.PaymentGatewayType;

/**
 * <p>This API is intended to define the specific configuration parameters
 * that this gateway implementation currently supports.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface PaymentGatewayConfiguration {

    /**
     * <p> Gets the configured transaction type for this module. </p>
     * <p> The possible initial transaction types for a gateway can be:
     * 'Authorize' or 'Authorize and Capture'</p>
     *
     * <p>This property is intended to be configurable</p>
     *
     * @see {@link org.broadleafcommerce.common.payment.PaymentTransactionType}
     */
    public boolean isPerformAuthorizeAndCapture();

    /**
     * <p> Sets the transaction type to 'AUTHORIZE AND CAPTURE'
     * for this gateway. If this is set to 'FALSE', then the gateway
     * will only issue an 'AUTHORIZATION' request.</p>
     *
     * <p>This property is intended to be configurable</p>
     *
     * @see {@link org.broadleafcommerce.common.payment.PaymentTransactionType}
     */
    public void setPerformAuthorizeAndCapture(boolean performAuthorizeAndCapture);

    /**
     * <p>All payment gateway classes that intend to make an external call, either manually
     * from an HTTP Post or through an SDK which makes its own external call, should
     * extend the AbstractExternalPaymentGatewayCall class. One of the configuration parameters
     * is the failure reporting threshold.</p>
     *
     * @see {@link AbstractExternalPaymentGatewayCall}
     */
    public int getFailureReportingThreshold();

    /**
     * <p>All payment gateway classes that intend to make an external call, either manually
     * from an HTTP Post or through an SDK which makes its own external call, should
     * extend the AbstractExternalPaymentGatewayCall class. One of the configuration parameters
     * is the failure reporting threshold.</p>
     *
     * @see {@link AbstractExternalPaymentGatewayCall}
     */
    public void setFailureReportingThreshold(int failureReportingThreshold);

    public boolean handlesAuthorize();

    public boolean handlesCapture();

    public boolean handlesAuthorizeAndCapture();

    public boolean handlesReverseAuthorize();

    public boolean handlesVoid();

    public boolean handlesRefund();

    public boolean handlesPartialCapture();

    public boolean handlesMultipleShipment();

    public boolean handlesRecurringPayment();

    public boolean handlesSavedCustomerPayment();
    
    /**
     * <p>Denotes whether or not this payment provider supports multiple payments on an order. For instance, a gift card provider
     * might want to support multiple gift cards on a single order but a credit card provider may not support payment with
     * multiple credit cards.</p>
     * 
     * <p>If a provider does not support multiple payments in an order then that means that all payments are deleted (archived)
     * on an order whenever a new payment of that type is attempted to be added to the order.</p>
     * 
     * @see {@link PaymentGatewayCheckoutService}
     */
    public boolean handlesMultiplePayments();
    
    /**
     * <p>Each payment module should have a unique subclass of {@link PaymentGatewayType} with only a single type. For instance,
     * the Braintree module would have a 'BraintreePaymentGatewayType' subclass which adds itself to the global static map.</p>
     * 
     * <p>In order to ensure that the class loader loads the extension of {@link PaymentGatewayType}, it is recommended
     * to add a simple bean definition to a module application context that is utilized by both the site and admin. Using
     * the Braintree module as an example again, this might look like:
     * 
     * <pre>
     * {@code
     * <bean class="com.broadleafcommerce.payment.service.gateway.BraintreeGatewayType" />
     * }
     * </pre>
     * </p>
     */
    public PaymentGatewayType getGatewayType();

}
