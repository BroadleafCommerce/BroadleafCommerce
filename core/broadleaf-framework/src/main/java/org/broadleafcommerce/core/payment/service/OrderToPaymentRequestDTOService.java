/*
 * #%L
 * BroadleafCommerce Framework
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

package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public interface OrderToPaymentRequestDTOService {

    /**
     * <p>This translates an Order into a PaymentRequestDTO.
     * This method assumes that the total transaction amount being sent to the gateway will be calculated from the
     * "final payment" on the order. This means that the transaction amount for
     * the returned {@link PaymentRequestDTO} will be {@link Order#getTotalAfterAppliedPayments()}
     * It assumes that all other payments (e.g. gift cards/account credit) have already
     * been applied to the {@link Order}.</p>
     *
     * @param order the {@link Order} to be translated
     * @return a {@link PaymentRequestDTO} based on the properties of an <b>order</b>.
     */
    public PaymentRequestDTO translateOrder(Order order);

    /**
     * Utilizes the {@link PaymentTransaction#getAdditionalFields()} map to populate necessary request parameters on the
     * resulting {@link PaymentRequestDTO}. These additional fields are then used by the payment gateway to construct
     * additional requests. For example, an existing {@link org.broadleafcommerce.core.payment.domain.PaymentTransaction} of
     * type {@link org.broadleafcommerce.common.payment.PaymentTransactionType#AUTHORIZE} might be passed into this method
     * in order for the gateway issue a "reverse auth" against this original transaction.
     *
     * Important: As of 4.0.1-GA+ the transaction amount passed in will <b>not</b< be set as the transaction total
     * on the PaymentRequestDTO if coming from a "payment flow". That is, if you are invoking this method via the
     * {@link org.broadleafcommerce.core.checkout.service.workflow.ValidateAndConfirmPaymentActivity} and the
     * payment transaction passed in is of type {@link org.broadleafcommerce.common.payment.PaymentTransactionType#UNCONFIRMED}.
     * If it is an unconfirmed payment, the transaction total will be set from the "final payment" details that
     * are coming off the order itself (along with other details like shipping/billing info etc...)
     * @see {@link https://github.com/BroadleafCommerce/BroadleafCommerce/issues/1423} for details.
     * 
     * @param transactionAmount the amount that should be placed on {@link PaymentRequestDTO#getTransactionTotal()}
     * @param paymentTransaction the transaction whose additional fields should be placed on {@link PaymentRequestDTO#getAdditionalFields()}
     *                           for the gateway to use
     * @return a new {@link PaymentRequestDTO} populated with the additional fields from <b>paymentTransaction</b> and
     *         the amount from <b>transactionAmount<b> OR override with final payment details on the order if coming in from a
     *         payment flow.
     * @see {@link org.broadleafcommerce.core.checkout.service.workflow.ValidateAndConfirmPaymentActivity}
     * @see {@link org.broadleafcommerce.core.checkout.service.workflow.ConfirmPaymentsRollbackHandler}
     */
    public PaymentRequestDTO translatePaymentTransaction(Money transactionAmount, PaymentTransaction paymentTransaction);

    /**
     * Uses total information on the Order to populate the
     * {@link org.broadleafcommerce.common.payment.dto.PaymentRequestDTO#transactionTotal(String)}()}
     * {@link org.broadleafcommerce.common.payment.dto.PaymentRequestDTO#taxTotal(String)}()}
     * {@link org.broadleafcommerce.common.payment.dto.PaymentRequestDTO#shippingTotal(String)}()}
     * {@link org.broadleafcommerce.common.payment.dto.PaymentRequestDTO#orderCurrencyCode(String)}()}
     * @param order the {@link Order} to get data from
     * @param requestDTO the {@link PaymentRequestDTO} that should be populated
     */
    public void populateTotals(Order order, PaymentRequestDTO requestDTO);

    /**
     * Uses customer information on the Order to populate the
     * {@link org.broadleafcommerce.common.payment.dto.PaymentRequestDTO#customer()} object
     * @param order the {@link Order} to get data from
     * @param requestDTO the {@link PaymentRequestDTO} that should be populated
     */
    public void populateCustomerInfo(Order order, PaymentRequestDTO requestDTO);
    
    /**
     * Uses the first shippable fulfillment group to populate the {@link PaymentRequestDTO#shipTo()} object
     * @param order the {@link Order} to get data from
     * @param requestDTO the {@link PaymentRequestDTO} that should be populated
     * @see {@link FulfillmentGroupService#getFirstShippableFulfillmentGroup(Order)}
     */
    public void populateShipTo(Order order, PaymentRequestDTO requestDTO);

    /**
     * Uses billing information on the Order to populate the
     * {@link org.broadleafcommerce.common.payment.dto.PaymentRequestDTO#billTo()} object
     * @param order the {@link Order} to get data from
     * @param requestDTO the {@link PaymentRequestDTO} that should be populated
     */
    public void populateBillTo(Order order, PaymentRequestDTO requestDTO);

    /**
     * <p>Uses order information to populate various line item and subtotal information on the order</p>
     *
     * <p>IMPORTANT: Each gateway that accepts line item information may require you to construct
     * this differently. Please consult the module documentation on how it should
     * be properly constructed.</p>
     *
     * <p>In this default implementation, just the subtotal is set, without any line item details.</p>
     *
     * @param order the {@link Order} to get data from
     * @param requestDTO the {@link PaymentRequestDTO} that should be populated
     */
    public void populateDefaultLineItemsAndSubtotal(Order order, PaymentRequestDTO requestDTO);
}
