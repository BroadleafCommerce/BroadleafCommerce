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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class PaymentRequestDTO {

    protected GatewayCustomerDTO<PaymentRequestDTO> customer;
    protected AddressDTO<PaymentRequestDTO> shipTo;
    protected AddressDTO<PaymentRequestDTO> billTo;
    protected CreditCardDTO<PaymentRequestDTO> creditCard;
    protected List<GiftCardDTO<PaymentRequestDTO>> giftCards;
    protected List<CustomerCreditDTO<PaymentRequestDTO>> customerCredits;
    protected List<LineItemDTO> lineItems;
    protected Map<String, Object> additionalFields;

    protected String transactionType;
    protected String orderId;
    protected String orderCurrencyCode;
    protected String orderDescription;
    protected String orderSubtotal;
    protected String shippingTotal;
    protected String taxTotal;
    protected String orderTotal;

    public PaymentRequestDTO() {
        this.giftCards = new ArrayList<GiftCardDTO<PaymentRequestDTO>>();
        this.customerCredits = new ArrayList<CustomerCreditDTO<PaymentRequestDTO>>();
        this.lineItems = new ArrayList<LineItemDTO>();
        this.additionalFields = new HashMap<String, Object>();
    }

    public GatewayCustomerDTO<PaymentRequestDTO> customer() {
        customer = new GatewayCustomerDTO<PaymentRequestDTO>(this);
        return customer;
    }

    public CreditCardDTO<PaymentRequestDTO> creditCard() {
        creditCard = new CreditCardDTO<PaymentRequestDTO>(this);
        return creditCard;
    }

    public AddressDTO<PaymentRequestDTO> shipTo() {
        shipTo = new AddressDTO<PaymentRequestDTO>(this);
        return shipTo;
    }

    public AddressDTO<PaymentRequestDTO> billTo() {
        billTo = new AddressDTO<PaymentRequestDTO>(this);
        return billTo;
    }

    public GiftCardDTO<PaymentRequestDTO> giftCard() {
        GiftCardDTO<PaymentRequestDTO> giftCardDTO = new GiftCardDTO<PaymentRequestDTO>(this);
        giftCards.add(giftCardDTO);
        return giftCardDTO;
    }

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

    public PaymentRequestDTO transactionType(String transactionType) {
        this.transactionType = transactionType;
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

    public PaymentRequestDTO orderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
        return this;
    }

    public String getTransactionType() {
        return transactionType;
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

    public AddressDTO getShipTo() {
        return shipTo;
    }

    public AddressDTO getBillTo() {
        return billTo;
    }

    public CreditCardDTO getCreditCard() {
        return creditCard;
    }

    public GatewayCustomerDTO getCustomer() {
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

    public String getOrderTotal() {
        return orderTotal;
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

}
