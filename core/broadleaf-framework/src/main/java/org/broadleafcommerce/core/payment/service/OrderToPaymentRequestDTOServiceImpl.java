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
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPhone;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blOrderToPaymentRequestDTOService")
public class OrderToPaymentRequestDTOServiceImpl implements OrderToPaymentRequestDTOService {

    public static final String ZERO_TOTAL = "0";
    
    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fgService;

    @Override
    public PaymentRequestDTO translateOrder(Order order) {
        if (order != null) {
            PaymentRequestDTO requestDTO = new PaymentRequestDTO()
                    .orderId(order.getId().toString());
            if (order.getCurrency() != null) {
                requestDTO.orderCurrencyCode(order.getCurrency().getCurrencyCode());
            }

            populateCustomerInfo(order, requestDTO);
            populateShipTo(order, requestDTO);
            populateBillTo(order, requestDTO);
            populateTotals(order, requestDTO);
            populateDefaultLineItemsAndSubtotal(order, requestDTO);

            return requestDTO;
        }

        return null;
    }

    @Override
    public PaymentRequestDTO translatePaymentTransaction(Money transactionAmount, PaymentTransaction paymentTransaction) {
        //Will set the full amount to be charged on the transaction total/subtotal and not worry about shipping/tax breakdown
        PaymentRequestDTO requestDTO = new PaymentRequestDTO()
            .transactionTotal(transactionAmount.getAmount().toPlainString())
            .orderSubtotal(transactionAmount.getAmount().toPlainString())
            .shippingTotal(ZERO_TOTAL)
            .taxTotal(ZERO_TOTAL)
            .orderCurrencyCode(paymentTransaction.getOrderPayment().getCurrency().getCurrencyCode())
            .orderId(paymentTransaction.getOrderPayment().getOrder().getId().toString());
        
        //Copy Additional Fields from PaymentTransaction into the Request DTO.
        //This will contain any gateway specific information needed to perform actions on this transaction
        Map<String, String> additionalFields = paymentTransaction.getAdditionalFields();
        for (String key : additionalFields.keySet()) {
            requestDTO.additionalField(key, additionalFields.get(key));
        }

        return requestDTO;
    }

    protected void populateTotals(Order order, PaymentRequestDTO requestDTO) {
        String total = ZERO_TOTAL;
        String shippingTotal = ZERO_TOTAL;
        String taxTotal = ZERO_TOTAL;

        if (order.getTotalAfterAppliedPayments() != null) {
            total = order.getTotalAfterAppliedPayments().toString();
        }

        if (order.getTotalShipping() != null) {
            shippingTotal = order.getTotalShipping().toString();
        }

        if (order.getTotalTax() != null) {
            taxTotal = order.getTotalTax().toString();
        }

        requestDTO.transactionTotal(total)
                .shippingTotal(shippingTotal)
                .taxTotal(taxTotal)
                .orderCurrencyCode(order.getCurrency().getCurrencyCode());
    }

    protected void populateCustomerInfo(Order order, PaymentRequestDTO requestDTO) {
        Customer customer = order.getCustomer();
        String phoneNumber = null;
        if (customer.getCustomerPhones() != null && !customer.getCustomerPhones().isEmpty()) {
            for (CustomerPhone phone : customer.getCustomerPhones()) {
                if (phone.getPhone().isDefault()) {
                    phoneNumber =  phone.getPhone().getPhoneNumber();
                }
            }
        }

        String orderEmail = (customer.getEmailAddress() == null)? order.getEmailAddress() : customer.getEmailAddress();

        requestDTO.customer()
                .customerId(customer.getId().toString())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(orderEmail)
                .phone(phoneNumber);

    }

    /**
     * Uses the first shippable fulfillment group to populate the {@link PaymentRequestDTO#shipTo()} object
     * @param order the {@link Order} to get data from
     * @param requestDTO the {@link PaymentRequestDTO} that should be populated
     * @see {@link FulfillmentGroupService#getFirstShippableFulfillmentGroup(Order)}
     */
    protected void populateShipTo(Order order, PaymentRequestDTO requestDTO) {
        List<FulfillmentGroup> fgs = order.getFulfillmentGroups();
        if (fgs != null && fgs.size() > 0) {
            FulfillmentGroup defaultFg = fgService.getFirstShippableFulfillmentGroup(order);
            if (defaultFg != null && defaultFg.getAddress() != null) {
                Address fgAddress = defaultFg.getAddress();
                String stateAbbr = null;
                String countryAbbr = null;
                String phone = null;

                if (fgAddress.getState() != null) {
                    stateAbbr = fgAddress.getState().getAbbreviation();
                }

                if (fgAddress.getCountry() != null) {
                    countryAbbr = fgAddress.getCountry().getAbbreviation();
                }

                if (fgAddress.getPhonePrimary() != null) {
                    phone = fgAddress.getPhonePrimary().getPhoneNumber();
                }

                requestDTO.shipTo()
                        .addressFirstName(fgAddress.getFirstName())
                        .addressLastName(fgAddress.getLastName())
                        .addressCompanyName(fgAddress.getCompanyName())
                        .addressLine1(fgAddress.getAddressLine1())
                        .addressLine2(fgAddress.getAddressLine2())
                        .addressCityLocality(fgAddress.getCity())
                        .addressStateRegion(stateAbbr)
                        .addressPostalCode(fgAddress.getPostalCode())
                        .addressCountryCode(countryAbbr)
                        .addressPhone(phone)
                        .addressEmail(fgAddress.getEmailAddress());
            }
        }
    }

    protected void populateBillTo(Order order, PaymentRequestDTO requestDTO) {
        for (OrderPayment payment : order.getPayments()) {
            if (payment.isActive() && PaymentType.CREDIT_CARD.equals(payment.getType())) {
                Address billAddress = payment.getBillingAddress();
                if (billAddress != null) {
                    String stateAbbr = null;
                    String countryAbbr = null;
                    String phone = null;

                    if (billAddress.getState() != null) {
                        stateAbbr = billAddress.getState().getAbbreviation();
                    }

                    if (billAddress.getCountry() != null) {
                        countryAbbr = billAddress.getCountry().getAbbreviation();
                    }

                    if (billAddress.getPhonePrimary() != null) {
                        phone = billAddress.getPhonePrimary().getPhoneNumber();
                    }

                    requestDTO.billTo()
                            .addressFirstName(billAddress.getFirstName())
                            .addressLastName(billAddress.getLastName())
                            .addressCompanyName(billAddress.getCompanyName())
                            .addressLine1(billAddress.getAddressLine1())
                            .addressLine2(billAddress.getAddressLine2())
                            .addressCityLocality(billAddress.getCity())
                            .addressStateRegion(stateAbbr)
                            .addressPostalCode(billAddress.getPostalCode())
                            .addressCountryCode(countryAbbr)
                            .addressPhone(phone)
                            .addressEmail(billAddress.getEmailAddress());
                }
            }
        }
    }



    /**
     * IMPORTANT:
     * <p>If you would like to pass Line Item information to a payment gateway
     * so that it shows up on the hosted site, you will need to override this method and
     * construct line items to conform to the requirements of that particular gateway:</p>
     *
     * <p>For Example: The Paypal Express Checkout NVP API validates that the order subtotal that you pass in,
     * add up to the amount of the line items that you pass in. So,
     * In that case you will need to take into account any additional fees, promotions,
     * credits, gift cards, etc... that are applied to the payment and add them
     * as additional line items with a negative amount when necessary.</p>
     *
     * <p>Each gateway that accepts line item information may require you to construct
     * this differently. Please consult the module documentation on how it should
     * be properly constructed.</p>
     *
     * <p>In this default implementation, just the subtotal is set, without any line item details.</p>
     *
     * @param order
     * @param requestDTO
     */
    protected void populateDefaultLineItemsAndSubtotal(Order order, PaymentRequestDTO requestDTO) {
        String subtotal = ZERO_TOTAL;
        if (order.getSubTotal() != null) {
            subtotal = order.getSubTotal().toString();
        }

        requestDTO.orderSubtotal(subtotal);
    }

}
