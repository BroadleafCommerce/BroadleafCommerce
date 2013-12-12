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
/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPhone;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blOrderToPaymentRequestDTOService")
public class OrderToPaymentRequestDTOServiceImpl implements OrderToPaymentRequestDTOService {

    @Override
    public PaymentRequestDTO translateOrder(Order order, PaymentGatewayType paymentGatewayType) {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO()
                .orderId(order.getId().toString())
                .orderCurrencyCode(order.getCurrency().getCurrencyCode())
                .transactionTotal(order.getTotal().toString())
                .orderSubtotal(order.getSubTotal().toString())
                .shippingTotal(order.getTotalShipping().toString())
                .taxTotal(order.getTotalTax().toString());

        populateCustomerInfo(order, requestDTO);
        populateShipTo(order, requestDTO);
        populateBillTo(order, paymentGatewayType, requestDTO);
        populateLineItems(order, requestDTO);

        return requestDTO;
    }

    @Override
    public PaymentRequestDTO translatePaymentTransaction(Money transactionAmount, PaymentTransaction paymentTransaction) {
        PaymentRequestDTO requestDTO = new PaymentRequestDTO()
                .transactionTotal(transactionAmount.toString());

        //Copy Additional Fields from PaymentTransaction into the Request DTO.
        //This will contain any gateway specific information needed to perform actions on this transaction
        Map<String, Serializable> additionalFields = paymentTransaction.getAdditionalFields();
        for (String key : additionalFields.keySet()) {
            requestDTO.additionalField(key, additionalFields.get(key));
        }

        return requestDTO;
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

        requestDTO.customer()
                .customerId(customer.getId().toString())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmailAddress())
                .phone(phoneNumber);

    }

    protected void populateShipTo(Order order, PaymentRequestDTO requestDTO) {
        List<FulfillmentGroup> fgs = order.getFulfillmentGroups();
        if (fgs != null && fgs.size() > 0) {
            FulfillmentGroup defaultFg = fgs.get(0);
            if (defaultFg.getAddress() != null) {
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

    protected void populateBillTo(Order order, PaymentGatewayType paymentGatewayType,
                                  PaymentRequestDTO requestDTO) {
        List<OrderPayment> payments = order.getPayments();
        for (OrderPayment payment : payments) {
            if (paymentGatewayType.equals(payment.getGatewayType())) {
                Address billAddress = payment.getBillingAddress();
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

    protected void populateLineItems(Order order, PaymentRequestDTO requestDTO) {
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            requestDTO.lineItem()
                    .shortDescription(orderItem.getName())
                    .systemId(orderItem.getId().toString())
                    .amount(orderItem.getTotalPrice().toString())
                    .quantity(orderItem.getQuantity() + "")
                    .done();
        }
    }

}
