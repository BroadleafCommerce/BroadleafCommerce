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

package org.broadleafcommerce.common.payment.dto;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>The DTO object that represents the response coming back from any call to the Gateway.
 * This can either wrap an API result call or a translated HTTP Web response.
 * This can not only be the results of a transaction, but also a request for a Secure Token etc...</p>
 *
 * <p>Note: the success and validity flags are set to true by default, unless otherwise overridden by specific
 * gateway implementations</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class PaymentResponseDTO {

    /**
     * Any customer information that relates to this transaction
     */
    protected GatewayCustomerDTO<PaymentResponseDTO> customer;

    /**
     * If shipping information is captured on the gateway, the values sent back will be put here
     */
    protected AddressDTO<PaymentResponseDTO> shipTo;

    /**
     * The billing address associated with this transaction
     */
    protected AddressDTO<PaymentResponseDTO> billTo;

    /**
     * for sale/authorize transactions, this will be the Credit Card object that was charged. This data is useful for showing
     * on an order confirmation screen.
     */
    protected CreditCardDTO<PaymentResponseDTO> creditCard;

    /**
     * Any gift cards that have been processed. This data is useful for showing
     * on an order confirmation screen
     */
    protected List<GiftCardDTO<PaymentResponseDTO>> giftCards;

    /**
     * Any customer credit accounts that have been processed. This data is useful for showing
     * on an order confirmation screen
     */
    protected List<CustomerCreditDTO<PaymentResponseDTO>> customerCredits;

    /**
     * The Type of Payment that this transaction response represents
     */
    protected PaymentType paymentType;

    /**
     * The Transaction Type of the Payment that this response represents
     */
    protected PaymentTransactionType paymentTransactionType;

    /**
     * The Order ID that this transaction is associated with
     */
    protected String orderId;
    
    /**
     * If this was a Transaction request, it will be the amount that was sent back from the gateway
     */
    protected Money amount;
    
    /**
     * Whether or not the transaction on the gateway was successful. This should be provided by the gateway alone.
     */
    protected boolean successful = true;
    
    /**
     * Whether or not this response was tampered with. This used to verify that the response that was received on the
     * endpoint (which is intended to only be invoked from the payment gateway) actually came from the gateway and was not
     * otherwise maliciously invoked by a 3rd-party. 
     */
    protected boolean valid = true;

    /**
     * Whether or not this transaction is confirmed (i.e. the Gateway has processed the transaction).
     * In most cases, this will be true, as most Credit Card gateway integrations require
     * that it be the last step in the process.
     * However, there are certain integrations, (e.g. PayPal Express Checkout, BLC Gift Card Module)
     * that aren't the final step in the checkout process and allow the customer to review their order
     * or add another payment method to the order before final submission. In these cases, the response
     * will be not confirmed. The confirmation for these payments will happen in the Checkout Workflow
     * where all payments on the order that are not confirmed, should be confirmed.
     */
    protected boolean confirmed = true;

    /**
     * <p>Sets whether or not this module should complete checkout on callback.
     * In most Credit Card gateway implementation, this should be set to 'TRUE' and
     * should not be configurable as the gateway expects it to tbe the final step
     * in the checkout process.</p>
     *
     * <p>In gateways where it does not expect to be the last step in the checkout process,
     * for example BLC Gift Card Module, PayPal Express Checkout, etc... The callback from
     * the gateway can be configured whether or not to complete checkout.</p>
     */
    protected boolean completeCheckoutOnCallback = true;

    /**
     * A string representation of the response that came from the gateway. This should be a string serialization of
     * {@link #responseMap}.
     */
    protected String rawResponse;
    
    /**
     * A more convenient representation of {@link #rawResponse} to hold the response from the gateway.
     */
    protected Map<String, String> responseMap;

    public PaymentResponseDTO(PaymentType paymentType) {
        this.paymentType = paymentType;
        this.giftCards = new ArrayList<GiftCardDTO<PaymentResponseDTO>>();
        this.customerCredits = new ArrayList<CustomerCreditDTO<PaymentResponseDTO>>();
        this.responseMap = new HashMap<String, String>();
    }

    public GatewayCustomerDTO<PaymentResponseDTO> customer() {
        customer = new GatewayCustomerDTO<PaymentResponseDTO>(this);
        return customer;
    }

    public CreditCardDTO<PaymentResponseDTO> creditCard() {
        creditCard = new CreditCardDTO<PaymentResponseDTO>(this);
        return creditCard;
    }

    public AddressDTO<PaymentResponseDTO> shipTo() {
        shipTo = new AddressDTO<PaymentResponseDTO>(this);
        return shipTo;
    }

    public AddressDTO<PaymentResponseDTO> billTo() {
        billTo = new AddressDTO<PaymentResponseDTO>(this);
        return billTo;
    }

    public GiftCardDTO<PaymentResponseDTO> giftCard() {
        GiftCardDTO<PaymentResponseDTO> giftCardDTO = new GiftCardDTO<PaymentResponseDTO>(this);
        giftCards.add(giftCardDTO);
        return giftCardDTO;
    }

    public CustomerCreditDTO<PaymentResponseDTO> customerCredit() {
        CustomerCreditDTO<PaymentResponseDTO> customerCreditDTO = new CustomerCreditDTO<PaymentResponseDTO>(this);
        customerCredits.add(customerCreditDTO);
        return customerCreditDTO;
    }

    public PaymentResponseDTO responseMap(String key, String value) {
        responseMap.put(key, value);
        return this;
    }

    public PaymentResponseDTO orderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public PaymentResponseDTO amount(Money amount) {
        this.amount = amount;
        return this;
    }

    public PaymentResponseDTO paymentTransactionType(PaymentTransactionType paymentTransactionType) {
        this.paymentTransactionType = paymentTransactionType;
        return this;
    }

    public PaymentResponseDTO successful(boolean successful) {
        this.successful = successful;
        return this;
    }

    public PaymentResponseDTO completeCheckoutOnCallback(boolean completeCheckoutOnCallback) {
        this.completeCheckoutOnCallback = completeCheckoutOnCallback;
        return this;
    }

    public PaymentResponseDTO valid(boolean valid) {
        this.valid = valid;
        return this;
    }

    public PaymentResponseDTO confirmed(boolean confirmed) {
        this.confirmed = confirmed;
        return this;
    }

    public PaymentResponseDTO rawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
        return this;
    }

    public GatewayCustomerDTO<PaymentResponseDTO> getCustomer() {
        return customer;
    }

    public AddressDTO<PaymentResponseDTO> getShipTo() {
        return shipTo;
    }

    public AddressDTO<PaymentResponseDTO> getBillTo() {
        return billTo;
    }

    public List<GiftCardDTO<PaymentResponseDTO>> getGiftCards() {
        return giftCards;
    }

    public List<CustomerCreditDTO<PaymentResponseDTO>> getCustomerCredits() {
        return customerCredits;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public String getOrderId() {
        return orderId;
    }

    public Money getAmount() {
        return amount;
    }

    public PaymentTransactionType getPaymentTransactionType() {
        return paymentTransactionType;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isCompleteCheckoutOnCallback() {
        return completeCheckoutOnCallback;
    }

    public CreditCardDTO<PaymentResponseDTO> getCreditCard() {
        return creditCard;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public Map<String, String> getResponseMap() {
        return responseMap;
    }
}
