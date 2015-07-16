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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.service.ISOService;
import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.dto.AddressDTO;
import org.broadleafcommerce.common.payment.dto.GatewayCustomerDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.PaymentGatewayCheckoutService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayConfiguration;
import org.broadleafcommerce.common.web.payment.controller.PaymentGatewayAbstractController;
import org.broadleafcommerce.core.checkout.service.CheckoutService;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutResponse;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    
    private static final Log LOG = LogFactory.getLog(DefaultPaymentGatewayCheckoutService.class);

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

    @Resource(name = "blISOService")
    protected ISOService isoService;
    
    @Resource(name = "blPhoneService")
    protected PhoneService phoneService;

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Value("${default.payment.gateway.checkout.useGatewayBillingAddress}")
    protected boolean useBillingAddressFromGateway = true;
    
    @Override
    public Long applyPaymentToOrder(PaymentResponseDTO responseDTO, PaymentGatewayConfiguration config) {
        
        //Payments can ONLY be parsed into Order Payments if they are 'valid'
        if (!responseDTO.isValid()) {
            throw new IllegalArgumentException("Invalid payment responses cannot be parsed into the order payment domain");
        }
        
        if (config == null) {
            throw new IllegalArgumentException("Config service cannot be null");
        }
        
        Long orderId = Long.parseLong(responseDTO.getOrderId());
        Order order = orderService.findOrderById(orderId);
        
        if (!OrderStatus.IN_PROCESS.equals(order.getStatus()) && !OrderStatus.CSR_OWNED.equals(order.getStatus())) {
            throw new IllegalArgumentException("Cannot apply another payment to an Order that is not IN_PROCESS or CSR_OWNED");
        }
        
        Customer customer = order.getCustomer();
        if (customer.isAnonymous()) {
            GatewayCustomerDTO<PaymentResponseDTO> gatewayCustomer = responseDTO.getCustomer();
            if (StringUtils.isEmpty(customer.getFirstName()) && gatewayCustomer != null) {
                customer.setFirstName(gatewayCustomer.getFirstName());
            }
            if (StringUtils.isEmpty(customer.getLastName()) && gatewayCustomer != null) {
                customer.setLastName(gatewayCustomer.getLastName());
            }
            if (StringUtils.isEmpty(customer.getEmailAddress()) && gatewayCustomer != null) {
                customer.setEmailAddress(gatewayCustomer.getEmail());
            }
        }

        // If the gateway sends back an email address and the order does not contain one, set it.
        GatewayCustomerDTO<PaymentResponseDTO> gatewayCustomer = responseDTO.getCustomer();
        if (order.getEmailAddress() == null && gatewayCustomer != null) {
            order.setEmailAddress(gatewayCustomer.getEmail());
        }

        // If the gateway sends back Shipping Information, we will save that to the first shippable fulfillment group.
        populateShippingInfo(responseDTO, order);

        // ALWAYS create a new order payment for the payment that comes in. Invalid payments should be cleaned up by
        // invoking {@link #markPaymentAsInvalid}.
        OrderPayment payment = orderPaymentService.create();
        payment.setType(responseDTO.getPaymentType());
        payment.setPaymentGatewayType(responseDTO.getPaymentGatewayType());
        payment.setAmount(responseDTO.getAmount());

        // If this gateway does not support multiple payments then mark all of the existing payments
        // as invalid before adding the new one
        List<OrderPayment> paymentsToInvalidate = new ArrayList<OrderPayment>();
        Address tempBillingAddress = null;
        if (!config.handlesMultiplePayments()) {
            PaymentGatewayType gateway = config.getGatewayType();
            for (OrderPayment p : order.getPayments()) {
                // A Payment on the order will be invalidated if:
                // - It's a temporary order payment: There may be a temporary Order Payment on the Order (e.g. to save the billing address)
                // - The payment being added is a Final Payment and there already exists a Final Payment
                // - The payment being added has the same gateway type of an existing one.
                if (PaymentGatewayType.TEMPORARY.equals(p.getGatewayType()) ||
                        (p.isFinalPayment() && payment.isFinalPayment()) ||
                        (p.getGatewayType() != null && p.getGatewayType().equals(gateway))) {

                    paymentsToInvalidate.add(p);

                    if (PaymentGatewayType.TEMPORARY.equals(p.getGatewayType()) ) {
                        tempBillingAddress = p.getBillingAddress();
                    }
                }
            }
        }

        for (OrderPayment invalid : paymentsToInvalidate) {
            // 2
            markPaymentAsInvalid(invalid.getId());
        }

        // The billing address that will be saved on the order will be parsed off the
        // Response DTO sent back from the Gateway as it may have Address Verification or Standardization.
        // If you do not wish to use the Billing Address coming back from the Gateway, you can override the
        // populateBillingInfo() method or set the useBillingAddressFromGateway property.
        populateBillingInfo(responseDTO, payment, tempBillingAddress);
        
        // Create the transaction for the payment
        PaymentTransaction transaction = orderPaymentService.createTransaction();
        transaction.setAmount(responseDTO.getAmount());
        transaction.setRawResponse(responseDTO.getRawResponse());
        transaction.setSuccess(responseDTO.isSuccessful());
        transaction.setType(responseDTO.getPaymentTransactionType());
        for (Entry<String, String> entry : responseDTO.getResponseMap().entrySet()) {
            transaction.getAdditionalFields().put(entry.getKey(), entry.getValue());
        }

        //Set the Credit Card Info on the Additional Fields Map
        if (responseDTO.getCreditCard() != null && responseDTO.getCreditCard().creditCardPopulated()) {

            transaction.getAdditionalFields().put(PaymentAdditionalFieldType.NAME_ON_CARD.getType(),
                    responseDTO.getCreditCard().getCreditCardHolderName());
            transaction.getAdditionalFields().put(PaymentAdditionalFieldType.CARD_TYPE.getType(),
                    responseDTO.getCreditCard().getCreditCardType());
            transaction.getAdditionalFields().put(PaymentAdditionalFieldType.EXP_DATE.getType(),
                    responseDTO.getCreditCard().getCreditCardExpDate());
            transaction.getAdditionalFields().put(PaymentAdditionalFieldType.LAST_FOUR.getType(),
                    responseDTO.getCreditCard().getCreditCardLastFour());
        }
        
        //TODO: validate that this particular type of transaction can be added to the payment (there might already
        // be an AUTHORIZE transaction, for instance)
        //Persist the order payment as well as its transaction
        payment.setOrder(order);
        transaction.setOrderPayment(payment);
        payment.addTransaction(transaction);
        payment = orderPaymentService.save(payment);

        if (transaction.getSuccess()) {
            orderService.addPaymentToOrder(order, payment, null);
        } else {
            // We will have to mark the entire payment as invalid and boot the user to re-enter their
            // billing info and payment information as there may be an error either with the billing address/or credit card
            handleUnsuccessfulTransaction(payment);
        }
        
        return payment.getId();
    }

    protected void populateBillingInfo(PaymentResponseDTO responseDTO, OrderPayment payment, Address tempBillingAddress) {
        Address billingAddress = tempBillingAddress;
        if (responseDTO.getBillTo() != null && isUseBillingAddressFromGateway()) {
            billingAddress = addressService.create();
            AddressDTO<PaymentResponseDTO> billToDTO = responseDTO.getBillTo();
            populateAddressInfo(billingAddress, billToDTO);
        }

        payment.setBillingAddress(billingAddress);
    }
    
    protected void populateShippingInfo(PaymentResponseDTO responseDTO, Order order) {
        FulfillmentGroup shippableFulfillmentGroup = fulfillmentGroupService.getFirstShippableFulfillmentGroup(order);
        Address shippingAddress = null;
        if (responseDTO.getShipTo() != null && shippableFulfillmentGroup != null) {
            shippingAddress = addressService.create();
            AddressDTO<PaymentResponseDTO> shipToDTO = responseDTO.getShipTo();
            populateAddressInfo(shippingAddress, shipToDTO);
            
            shippableFulfillmentGroup = fulfillmentGroupService.findFulfillmentGroupById(shippableFulfillmentGroup.getId());
            if (shippableFulfillmentGroup != null) {
                shippableFulfillmentGroup.setAddress(shippingAddress);
                fulfillmentGroupService.save(shippableFulfillmentGroup);
            }
        }
    }
    
    protected void populateAddressInfo(Address address, AddressDTO<PaymentResponseDTO> dto) {
        address.setFirstName(dto.getAddressFirstName());
        address.setLastName(dto.getAddressLastName());
        address.setFullName(dto.getAddressFirstName() + " " + dto.getAddressLastName());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getAddressCityLocality());

        State state = null;
        if(dto.getAddressStateRegion() != null) {
            state = stateService.findStateByAbbreviation(dto.getAddressStateRegion());
        }
        if (state == null) {
            LOG.warn("The given state from the response: " + dto.getAddressStateRegion() + " could not be found"
                    + " as a state abbreviation in BLC_STATE");
        }
        address.setState(state);
        address.setStateProvinceRegion(dto.getAddressStateRegion());

        address.setPostalCode(dto.getAddressPostalCode());

        Country country = null;
        ISOCountry isoCountry = null;
        if (dto.getAddressCountryCode() != null) {
            country = countryService.findCountryByAbbreviation(dto.getAddressCountryCode());
            isoCountry = isoService.findISOCountryByAlpha2Code(dto.getAddressCountryCode());
        }
        if (country == null) {
            LOG.warn("The given country from the response: " + dto.getAddressCountryCode() + " could not be found"
                    + " as a country abbreviation in BLC_COUNTRY");
        } else if (isoCountry == null) {
            LOG.error("The given country from the response: " + dto.getAddressCountryCode() + " could not be found"
                    + " as a country alpha-2 code in BLC_ISO_COUNTRY");
        }

        address.setCountry(country);
        address.setIsoCountryAlpha2(isoCountry);

        if (dto.getAddressPhone() != null) {
            Phone billingPhone = phoneService.create();
            billingPhone.setPhoneNumber(dto.getAddressPhone());
            address.setPhonePrimary(billingPhone);
        }
    }

    /**
     * This default implementation will mark the entire payment as invalid and boot the user to re-enter their
     * billing info and payment information as there may be an error with either the billing address or credit card.
     * This is the safest method, because depending on the implementation of the Gateway, we may not know exactly where
     * the error occurred (e.g. Address Verification enabled, etc...) So, we will assume that the error invalidates
     * the entire Order Payment, and the customer will have to re-enter their billing and credit card information to be
     * processed again.
     *
     * @param payment
     */
    protected void handleUnsuccessfulTransaction(OrderPayment payment) {
        markPaymentAsInvalid(payment.getId());
    }

    @Override
    public void markPaymentAsInvalid(Long orderPaymentId) {
        OrderPayment payment = orderPaymentService.readPaymentById(orderPaymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Could not find payment with id " + orderPaymentId);
        }
        // Do not do an actual delete here, otherwise Hibernate will screw up the relationships by setting parent transactions
        // to null because of the cascades. This manifests itself when you have an AUTHORIZE_AND_CAPTURE transaction and
        // then an immediate VOID (like if there is an exception in the checkout workflow). The VOID transaction should
        // have its parent set to the AUTHORIZE_AND_CAPTURE transaction which works up until we call Hibernate's delete
        // on the payment. By cascading down to the transaction, Hibernate goes and removes the parentTransaction relationship
        // from the VOID transaction
        // The fix is to set archived statuses manually and not rely on Hibernate's @SqlDelete
        payment.setArchived('Y');
        for (PaymentTransaction transaction : payment.getTransactions()) {
            transaction.setArchived('Y');
        }
    }

    @Override
    public String initiateCheckout(Long orderId) throws Exception{
        Order order = orderService.findOrderById(orderId, true);
        if (order == null || order instanceof NullOrderImpl) {
            throw new IllegalArgumentException("Could not order with id " + orderId);
        }
        
        CheckoutResponse response;

        try {
            response = checkoutService.performCheckout(order);
        } catch (CheckoutException e) {
            throw new Exception(e);
        }

        if (response.getOrder().getOrderNumber() == null) {
            LOG.error("Order Number for Order ID: " + order.getId() + " is null.");
        }

        return response.getOrder().getOrderNumber();
    }

    @Override
    public String lookupOrderNumberFromOrderId(PaymentResponseDTO responseDTO) {
        Order order = orderService.findOrderById(Long.parseLong(responseDTO.getOrderId()), true);
        if (order == null) {
            throw new IllegalArgumentException("An order with ID " + responseDTO.getOrderId() + " cannot be found for the" +
            		" given payment response.");
        }
        return order.getOrderNumber();
    }

    public boolean isUseBillingAddressFromGateway() {
        return useBillingAddressFromGateway;
    }

    public void setUseBillingAddressFromGateway(boolean useBillingAddressFromGateway) {
        this.useBillingAddressFromGateway = useBillingAddressFromGateway;
    }
}
