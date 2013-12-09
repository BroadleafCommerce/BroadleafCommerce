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
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.payment.service;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.dto.GatewayCustomerDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayCheckoutService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfigurationService;
import org.broadleafcommerce.common.web.payment.controller.PaymentGatewayAbstractController;
import org.broadleafcommerce.core.checkout.service.CheckoutService;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutResponse;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.PhoneService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map.Entry;

import javax.annotation.Resource;


/**
 * Core framework implementation of the {@link PaymentGatewayCheckoutService}.
 * 
 * @see {@link PaymentGatewayAbstractController}
 * @author Phillip Verheyden (phillipuniverse)
 */
@Service("blPaymentGatewayCheckoutService")
public class DefaultPaymentGatewayCheckoutService implements PaymentGatewayCheckoutService {

    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Resource(name = "blOrderPaymentService")
    protected OrderPaymentService orderPaymentService;
    
    @Resource(name = "blCheckoutService")
    protected CheckoutService checkoutService;
    
    @Resource(name = "blAddressService")
    protected AddressService addressService;
    
    @Resource(name = "blStateService")
    protected StateService stateService;
    
    @Resource(name = "blCountryService")
    protected CountryService countryService;
    
    @Resource(name = "blPhoneService")
    protected PhoneService phoneService;
    
    @Override
    public Long applyPaymentToOrder(PaymentResponseDTO responseDTO, PaymentGatewayConfigurationService configService) {
        
        //Payments can ONLY be parsed into PaymentInfos if they are 'valid'
        if (!responseDTO.isValid()) {
            throw new IllegalArgumentException("Invalid payment responses cannot be parsed into the order payment domain");
        }
        
        if (configService == null) {
            throw new IllegalArgumentException("Config service cannot be null");
        }
        
        Long orderId = Long.parseLong(responseDTO.getOrderId());
        Order order = orderService.findOrderById(orderId);
        
        //TODO: ensure that the order has not already been checked out before applying payments to it
        
        Customer customer = order.getCustomer();
        if (customer.isAnonymous()) {
            GatewayCustomerDTO<PaymentResponseDTO> gatewayCustomer = responseDTO.getCustomer();
            if (StringUtils.isEmpty(customer.getFirstName())) {
                customer.setFirstName(gatewayCustomer.getFirstName());
            }
            if (StringUtils.isEmpty(customer.getLastName())) {
                customer.setLastName(gatewayCustomer.getLastName());
            }
            if (StringUtils.isEmpty(customer.getEmailAddress())) {
                customer.setEmailAddress(gatewayCustomer.getEmail());
            }
        }
        
        // If this gateway does not support multiple payments then mark all of the existing payments as invalid before adding
        // the new one
        if (!configService.handlesMultiplePayments()) {
            PaymentGatewayType gateway = configService.getGatewayType();
            for (OrderPayment payment : order.getPayments()) {
                if (payment.getGatewayType().equals(gateway)) {
                    markPaymentAsInvalid(payment.getId());
                }
            }
        }
        
        // ALWAYS create a new order payment for the payment that comes in. Invalid payments should be cleaned up by
        // invoking {@link #markPaymentaAsInvalid}.
        OrderPayment payment = orderPaymentService.create();
        payment.setType(responseDTO.getPaymentType());
        payment.setAmount(responseDTO.getAmount());
        
        Address billingAddress = addressService.create();
        billingAddress.setAddressLine1(responseDTO.getBillTo().getAddressLine1());
        billingAddress.setAddressLine2(responseDTO.getBillTo().getAddressLine2());
        billingAddress.setCity(responseDTO.getBillTo().getAddressCityLocality());
        
        //TODO: what happens if State and Country cannot be found?
        State state = stateService.findStateByAbbreviation(responseDTO.getBillTo().getAddressStateRegion());
        billingAddress.setState(state);
        Country country = countryService.findCountryByAbbreviation(responseDTO.getBillTo().getAddressCountryCode());
        billingAddress.setCountry(country);
        
        Phone billingPhone = phoneService.create();
        billingPhone.setPhoneNumber(responseDTO.getBillTo().getAddressPhone());
        billingAddress.setPhonePrimary(billingPhone);
        
        PaymentTransaction transaction = orderPaymentService.createTransaction();
        transaction.setAmount(responseDTO.getAmount());
        transaction.setRawResponse(responseDTO.getRawResponse());
        transaction.setSuccess(responseDTO.isSuccessful());
        
        //TODO: handle payments that have to be confirmed. Scenario:
        /*
         * 1. User goes through checkout
         * 2. User submits payment to gateway which supports a 'confirmation
         * 3. User is on review order page
         * 4. User goes back and makes modifications to their cart
         * 5. The user now has an order payment in the system which has been unconfirmed and is really in this weird, invalid
         *    state.
         * 6. 
         */
        
        //TODO: get the transaction type from the response DTO
        //transaction.setType(type);
        
        //TODO: copy additional fields from payment response into payment transaction
        
        //TODO: validate that this particular type of transaction is valid to be added to the payment (there might already
        // be an AUTHORIZE transaction, for instance)
        payment.addTransaction(transaction);
        payment = orderPaymentService.save(payment);
        orderService.addPaymentToOrder(order, payment, null);
        
        return payment.getId();
    }

    @Override
    public void markPaymentAsInvalid(Long orderPaymentId) {
        // TODO delete (which archives) the given payment id
    }

    @Override
    public String initiateCheckout(Long orderId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String lookupOrderNumberFromOrderId(PaymentResponseDTO responseDTO) {
        Order order = orderService.findOrderById(Long.parseLong(responseDTO.getOrderId()));
        if (order == null) {
            throw new IllegalArgumentException("An order with ID " + responseDTO.getOrderId() + " cannot be found for the" +
            		" given payment response.");
        }
        return order.getOrderNumber();
    }

}
