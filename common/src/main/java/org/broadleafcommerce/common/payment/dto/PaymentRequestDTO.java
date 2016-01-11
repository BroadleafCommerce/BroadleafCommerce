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

import org.broadleafcommerce.common.payment.PaymentGatewayRequestType;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *     A DTO that is comprised of all the information that is sent to a Payment Gateway
 *     to complete a transaction. This DTO uses a modified builder pattern in order to
 *     provide an easy way of constructing the request. You can construct a DTO
 *     using the following notation:
 * </p>
 * <p>
 *     IMPORTANT: note that some of the convenience methods generate a new instance of the object.
 *     (e.g. billTo, shipTo, etc...) So, if you need to modify the shipping or billing information
 *     after you have invoked requestDTO.shipTo()..., use the getShipTo() method to append more information.
 *     Otherwise, you will overwrite the shipping information with a new instance.
 * </p>
 *
 * <pre><code>
 *      PaymentRequestDTO requestDTO = new PaymentRequestDTO()
 *          .orderId(referenceNumber)
 *          .customer()
 *              .customerId("1")
 *              .done()
 *          .shipTo()
 *              .addressFirstName("Bill")
 *              .addressLastName("Broadleaf")
 *              .addressLine1("123 Test Dr.")
 *              .addressCityLocality("Austin")
 *              .addressStateRegion("TX")
 *              .addressPostalCode("78759")
 *              .done()
 *          .billTo()
 *              .addressFirstName("Bill")
 *              .addressLastName("Broadleaf")
 *              .addressLine1("123 Test Dr.")
 *              .addressCityLocality("Austin")
 *              .addressStateRegion("TX")
 *              .addressPostalCode("78759")
 *              .done()
 *          .shippingTotal("0")
 *          .taxTotal("0")
 *          .orderCurrencyCode("USD")
 *          .orderDescription("My Order Description")
 *          .orderSubtotal("10.00")
 *          .transactionTotal("10.00")
 *          .lineItem()
 *              .name("My Product")
 *              .description("My Product Description")
 *              .shortDescription("My Product Short Description")
 *              .systemId("1")
 *              .amount("10.00")
 *              .quantity("1")
 *              .itemTotal("10.00")
 *              .tax("0")
 *              .total("10.00")
 *              .done();
 * </code></pre>
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class PaymentRequestDTO {

    protected GatewayCustomerDTO<PaymentRequestDTO> customer;
    protected AddressDTO<PaymentRequestDTO> shipTo;
    protected AddressDTO<PaymentRequestDTO> billTo;
    protected CreditCardDTO<PaymentRequestDTO> creditCard;
    protected SubscriptionDTO<PaymentRequestDTO> subscription;
    protected List<GiftCardDTO<PaymentRequestDTO>> giftCards;
    protected List<CustomerCreditDTO<PaymentRequestDTO>> customerCredits;
    protected List<LineItemDTO> lineItems;
    protected Map<String, Object> additionalFields;

    protected String orderId;
    protected String orderCurrencyCode;
    protected String orderDescription;
    protected String orderSubtotal;
    protected String shippingTotal;
    protected String taxTotal;
    protected String transactionTotal;

    protected PaymentType paymentType;
    protected PaymentGatewayRequestType gatewayRequestType;

    protected boolean completeCheckoutOnCallback = true;

    public PaymentRequestDTO() {
        this.giftCards = new ArrayList<GiftCardDTO<PaymentRequestDTO>>();
        this.customerCredits = new ArrayList<CustomerCreditDTO<PaymentRequestDTO>>();
        this.lineItems = new ArrayList<LineItemDTO>();
        this.additionalFields = new HashMap<String, Object>();
    }

    /**
     * You should only call this once, as it will create a new customer
     * if called more than once. Use the getter if you need to append more information later.
     */
    public GatewayCustomerDTO<PaymentRequestDTO> customer() {
        customer = new GatewayCustomerDTO<PaymentRequestDTO>(this);
        return customer;
    }

    /**
     * You should only call this once, as it will create a new credit card
     * if called more than once. Use the getter if you need to append more information later.
     */
    public CreditCardDTO<PaymentRequestDTO> creditCard() {
        creditCard = new CreditCardDTO<PaymentRequestDTO>(this);
        return creditCard;
    }

    /**
     * You should only call this once, as it will create a new subscription
     * if called more than once. Use the getter if you need to append more information later.
     */
    public SubscriptionDTO<PaymentRequestDTO> subscription() {
        subscription = new SubscriptionDTO<PaymentRequestDTO>(this);
        return subscription;
    }

    /**
     * You should only call this once, as it will create a new customer
     * if called more than once. Use the getter if you need to append more information later.
     */
    public AddressDTO<PaymentRequestDTO> shipTo() {
        shipTo = new AddressDTO<PaymentRequestDTO>(this);
        return shipTo;
    }

    /**
     * You should only call this once, as it will create a new bill to address
     * if called more than once. Use the getter if you need to append more information later.
     */
    public AddressDTO<PaymentRequestDTO> billTo() {
        billTo = new AddressDTO<PaymentRequestDTO>(this);
        return billTo;
    }

    /**
     * You should only call this once, as it will create a new gift card
     * if called more than once. Use the getter if you need to append more information later.
     */
    public GiftCardDTO<PaymentRequestDTO> giftCard() {
        GiftCardDTO<PaymentRequestDTO> giftCardDTO = new GiftCardDTO<PaymentRequestDTO>(this);
        giftCards.add(giftCardDTO);
        return giftCardDTO;
    }

    /**
     * You should only call this once, as it will create a new gift card
     * if called more than once. Use the getter if you need to append more information later.
     */
    public CustomerCreditDTO<PaymentRequestDTO> customerCredit() {
        CustomerCreditDTO<PaymentRequestDTO> customerCreditDTO = new CustomerCreditDTO<PaymentRequestDTO>(this);
        customerCredits.add(customerCreditDTO);
        return customerCreditDTO;
    }

    public LineItemDTO lineItem() {
        return new LineItemDTO(this);
    }

    public PaymentRequestDTO additionalField(String key, Object value) {
        additionalFields.put(key, value);
        return this;
    }

    public PaymentRequestDTO orderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public PaymentRequestDTO orderCurrencyCode(String orderCurrencyCode) {
        this.orderCurrencyCode = orderCurrencyCode;
        return this;
    }

    public PaymentRequestDTO orderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
        return this;
    }

    public PaymentRequestDTO orderSubtotal(String orderSubtotal) {
        this.orderSubtotal = orderSubtotal;
        return this;
    }

    public PaymentRequestDTO shippingTotal(String shippingTotal) {
        this.shippingTotal = shippingTotal;
        return this;
    }

    public PaymentRequestDTO taxTotal(String taxTotal) {
        this.taxTotal = taxTotal;
        return this;
    }

    public PaymentRequestDTO transactionTotal(String transactionTotal) {
        this.transactionTotal = transactionTotal;
        return this;
    }

    public PaymentRequestDTO paymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
        return this;
    }

    public PaymentRequestDTO gatewayRequestType(PaymentGatewayRequestType gatewayRequestType) {
        this.gatewayRequestType = gatewayRequestType;
        return this;
    }

    public PaymentRequestDTO completeCheckoutOnCallback(boolean completeCheckoutOnCallback) {
        this.completeCheckoutOnCallback = completeCheckoutOnCallback;
        return this;
    }

    public List<LineItemDTO> getLineItems() {
        return lineItems;
    }

    public List<GiftCardDTO<PaymentRequestDTO>> getGiftCards() {
        return giftCards;
    }

    public List<CustomerCreditDTO<PaymentRequestDTO>> getCustomerCredits() {
        return customerCredits;
    }

    public AddressDTO<PaymentRequestDTO> getShipTo() {
        return shipTo;
    }

    public AddressDTO<PaymentRequestDTO> getBillTo() {
        return billTo;
    }

    public CreditCardDTO<PaymentRequestDTO> getCreditCard() {
        return creditCard;
    }

    public SubscriptionDTO<PaymentRequestDTO> getSubscription() {
        return subscription;
    }

    public GatewayCustomerDTO<PaymentRequestDTO> getCustomer() {
        return customer;
    }

    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderCurrencyCode() {
        return orderCurrencyCode;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public String getOrderSubtotal() {
        return orderSubtotal;
    }

    public String getShippingTotal() {
        return shippingTotal;
    }

    public String getTaxTotal() {
        return taxTotal;
    }

    public String getTransactionTotal() {
        return transactionTotal;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public PaymentGatewayRequestType getGatewayRequestType() {
        return gatewayRequestType;
    }

    public boolean isCompleteCheckoutOnCallback() {
        return completeCheckoutOnCallback;
    }

    public boolean shipToPopulated() {
        return (getShipTo() != null && getShipTo().addressPopulated());
    }

    public boolean billToPopulated() {
        return (getBillTo() != null && getBillTo().addressPopulated());
    }

    public boolean creditCardPopulated() {
        return (getCreditCard() != null && getCreditCard().creditCardPopulated());
    }

    public boolean customerPopulated() {
        return (getCustomer() != null && getCustomer().customerPopulated());
    }

    public boolean subscriptionPopulated() {
        return (getSubscription() != null && getSubscription().subscriptionPopulated());
    }

}
