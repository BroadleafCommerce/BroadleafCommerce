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

    protected GatewayCustomerDTO customer;
    protected CreditCardDTO creditCard;
    protected List<LineItemDTO> lineItems;
    protected Map<String, Object> additionalFields;

    protected String transactionType;

    protected String shipToFirstName;
    protected String shipToLastName;
    protected String shipToCompanyName;
    protected String shipToAddressLine1;
    protected String shipToAddressLine2;
    protected String shipToCityLocality;
    protected String shipToStateRegion;
    protected String shipToPostalCode;
    protected String shipToCountryCode;
    protected String shipToPhone;
    protected String shipToEmail;

    protected String billToFirstName;
    protected String billToLastName;
    protected String billToCompanyName;
    protected String billToAddressLine1;
    protected String billToAddressLine2;
    protected String billToCityLocality;
    protected String billToStateRegion;
    protected String billToPostalCode;
    protected String billToCountryCode;
    protected String billToPhone;
    protected String billToEmail;

    protected String orderId;
    protected String orderCurrencyCode;
    protected String orderDescription;
    protected String orderSubtotal;
    protected String shippingTotal;
    protected String taxTotal;
    protected String orderTotal;

    public PaymentRequestDTO() {
        this.lineItems = new ArrayList<LineItemDTO>();
        this.additionalFields = new HashMap<String, Object>();
    }

    public GatewayCustomerDTO customer() {
        customer = new GatewayCustomerDTO(this);
        return customer;
    }

    public CreditCardDTO creditCard() {
        creditCard = new CreditCardDTO(this);
        return creditCard;
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

    public PaymentRequestDTO shipToFirstName(String shipToFirstName) {
        this.shipToFirstName = shipToFirstName;
        return this;
    }

    public PaymentRequestDTO shipToLastName(String shipToLastName) {
        this.shipToLastName = shipToLastName;
        return this;
    }

    public PaymentRequestDTO shipToCompanyName(String shipToCompanyName) {
        this.shipToCompanyName = shipToCompanyName;
        return this;
    }

    public PaymentRequestDTO shipToAddressLine1(String shipToAddressLine1) {
        this.shipToAddressLine1 = shipToAddressLine1;
        return this;
    }

    public PaymentRequestDTO shipToAddressLine2(String shipToAddressLine2) {
        this.shipToAddressLine2 = shipToAddressLine2;
        return this;
    }

    public PaymentRequestDTO shipToCityLocality(String shipToCityLocality) {
        this.shipToCityLocality = shipToCityLocality;
        return this;
    }

    public PaymentRequestDTO shipToStateRegion(String shipToStateRegion) {
        this.shipToStateRegion = shipToStateRegion;
        return this;
    }

    public PaymentRequestDTO shipToPostalCode(String shipToPostalCode) {
        this.shipToPostalCode = shipToPostalCode;
        return this;
    }

    public PaymentRequestDTO shipToCountryCode(String shipToCountryCode) {
        this.shipToCountryCode = shipToCountryCode;
        return this;
    }

    public PaymentRequestDTO shipToPhone(String shipToPhone) {
        this.shipToPhone = shipToPhone;
        return this;
    }

    public PaymentRequestDTO shipToEmail(String shipToEmail) {
        this.shipToEmail = shipToEmail;
        return this;
    }

    public PaymentRequestDTO billToFirstName(String billToFirstName) {
        this.billToFirstName = billToFirstName;
        return this;
    }

    public PaymentRequestDTO billToLastName(String billToLastName) {
        this.billToLastName = billToLastName;
        return this;
    }
    public PaymentRequestDTO billToCompanyName(String billToCompanyName) {
        this.billToCompanyName = billToCompanyName;
        return this;
    }

    public PaymentRequestDTO billToAddressLine1(String billToAddressLine1) {
        this.billToAddressLine1 = billToAddressLine1;
        return this;
    }

    public PaymentRequestDTO billToAddressLine2(String billToAddressLine2) {
        this.billToAddressLine2 = billToAddressLine2;
        return this;
    }

    public PaymentRequestDTO billToCityLocality(String billToCityLocality) {
        this.billToCityLocality = billToCityLocality;
        return this;
    }

    public PaymentRequestDTO billToStateRegion(String billToStateRegion) {
        this.billToStateRegion = billToStateRegion;
        return this;
    }

    public PaymentRequestDTO billToPostalCode(String billToPostalCode) {
        this.billToPostalCode = billToPostalCode;
        return this;
    }

    public PaymentRequestDTO billToCountryCode(String billToCountryCode) {
        this.billToCountryCode = billToCountryCode;
        return this;
    }

    public PaymentRequestDTO billToPhone(String billToPhone) {
        this.billToPhone = billToPhone;
        return this;
    }

    public PaymentRequestDTO billToEmail(String billToEmail) {
        this.billToEmail = billToEmail;
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

    public CreditCardDTO getCreditCard() {
        return creditCard;
    }

    public GatewayCustomerDTO getCustomer() {
        return customer;
    }

    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    public String getShipToFirstName() {
        return shipToFirstName;
    }

    public String getShipToLastName() {
        return shipToLastName;
    }

    public String getShipToCompanyName() {
        return shipToCompanyName;
    }

    public String getShipToAddressLine1() {
        return shipToAddressLine1;
    }

    public String getShipToAddressLine2() {
        return shipToAddressLine2;
    }

    public String getShipToCityLocality() {
        return shipToCityLocality;
    }

    public String getShipToStateRegion() {
        return shipToStateRegion;
    }

    public String getShipToPostalCode() {
        return shipToPostalCode;
    }

    public String getShipToCountryCode() {
        return shipToCountryCode;
    }

    public String getShipToPhone() {
        return shipToPhone;
    }

    public String getShipToEmail() {
        return shipToEmail;
    }

    public String getBillToFirstName() {
        return billToFirstName;
    }

    public String getBillToLastName() {
        return billToLastName;
    }

    public String getBillToCompanyName() {
        return billToCompanyName;
    }

    public String getBillToAddressLine1() {
        return billToAddressLine1;
    }

    public String getBillToAddressLine2() {
        return billToAddressLine2;
    }

    public String getBillToCityLocality() {
        return billToCityLocality;
    }

    public String getBillToStateRegion() {
        return billToStateRegion;
    }

    public String getBillToPostalCode() {
        return billToPostalCode;
    }

    public String getBillToCountryCode() {
        return billToCountryCode;
    }

    public String getBillToPhone() {
        return billToPhone;
    }

    public String getBillToEmail() {
        return billToEmail;
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

    public Boolean billToPopulated() {
        return (getBillToFirstName() != null ||
                getBillToLastName() != null ||
                getBillToCompanyName() != null ||
                getBillToAddressLine1() != null ||
                getBillToAddressLine2() != null ||
                getBillToCityLocality() != null ||
                getBillToStateRegion() != null ||
                getBillToPostalCode() != null ||
                getBillToCountryCode() != null ||
                getBillToPhone() != null ||
                getBillToEmail() != null);
    }

    public Boolean shipToPopulated() {
        return (getShipToFirstName() != null ||
                getShipToLastName() != null ||
                getShipToCompanyName() != null ||
                getShipToAddressLine1() != null ||
                getShipToAddressLine2() != null ||
                getShipToCityLocality() != null ||
                getShipToStateRegion() != null ||
                getShipToPostalCode() != null ||
                getShipToCountryCode() != null ||
                getShipToPhone() != null ||
                getShipToEmail() != null);
    }

}
