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

/**
 * @author Elbert Bautista (elbertbautista)
 *
 * This API is intended to define the specific configuration parameters
 * that this gateway implementation currently supports.
 *
 */
public interface PaymentGatewayConfigurationService {

    public boolean completeCheckoutOnCallback();

    public boolean handlesAuthorize();

    public boolean handlesCapture();

    public boolean handlesAuthorizeAndCapture();

    public boolean handlesReverseAuthorize();

    public boolean handlesVoid();

    public boolean handlesRefund();

    public boolean handlesPartialCapture();

    public boolean handlesMultipleShipment();

    public boolean handlesTransactionConfirmation();

    public boolean handlesRecurringPayment();

    public boolean handlesSavedCustomerPayment();

    public int getFailureReportingThreshold();
    
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

}
