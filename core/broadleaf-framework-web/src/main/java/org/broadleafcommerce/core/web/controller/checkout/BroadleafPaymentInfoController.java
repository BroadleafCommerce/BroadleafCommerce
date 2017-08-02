/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */

package org.broadleafcommerce.core.web.controller.checkout;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.checkout.model.PaymentInfoForm;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.core.web.payment.service.SavedPaymentService;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Chris Kittrell (ckittrell)
 */
public class BroadleafPaymentInfoController extends AbstractCheckoutController {

    @Resource(name = "blSavedPaymentService")
    protected SavedPaymentService savedPaymentService;

    /**
     * Processes the request to save an {@link OrderPayment} based on an existing or new {@link CustomerPayment}.
     *
     * Note: this default Broadleaf implementation will create a CustomerPayment if one does not exist,
     *  and copy it's data to a new OrderPayment.
     *
     * @param request
     * @param response
     * @param model
     * @param paymentForm
     * @return the return path
     * @throws ServiceException
     */
    public String savePaymentInfo(HttpServletRequest request, HttpServletResponse response, Model model,
                                 PaymentInfoForm paymentForm, BindingResult result) throws PricingException, ServiceException {
        Order cart = CartState.getCart();
        Customer customer = CustomerState.getCustomer();

        preProcessBillingAddress(paymentForm, cart);
        paymentInfoFormValidator.validate(paymentForm, result);

        if (!result.hasErrors()) {
            if (paymentForm.getShouldSaveNewPayment() && !paymentForm.getShouldUseCustomerPayment()) {
                if (paymentForm.getCustomerPaymentId() != null){
                    savedPaymentService.updateSavedPayment(customer, paymentForm);
                } else if (paymentForm.getCustomerPaymentId() == null) {
                    Long customerPaymentId = savedPaymentService.addSavedPayment(customer, paymentForm);

                    paymentForm.setCustomerPaymentId(customerPaymentId);
                    paymentForm.setShouldUseCustomerPayment(true);
                }
            }

            if (paymentForm.getShouldUseCustomerPayment()) {
                CustomerPayment customerPayment = customerPaymentService.readCustomerPaymentById(paymentForm.getCustomerPaymentId());

                if (!cartStateService.cartHasCreditCardPaymentWithSameToken(customerPayment.getPaymentToken())) {
                    orderPaymentService.deleteOrderPaymentsByType(cart, PaymentType.CREDIT_CARD);
                    orderPaymentService.createOrderPaymentFromCustomerPayment(cart, customerPayment, cart.getTotalAfterAppliedPayments());
                }
            }
        }

        if (isAjaxRequest(request)) {
            //Add module specific model variables
            checkoutControllerExtensionManager.getProxy().addAdditionalModelVariables(model);
            return getCheckoutView();
        } else {
            return getCheckoutPageRedirect();
        }
    }

    /**
     * Processes the request to save a billing address.
     *
     * Note: this default Broadleaf implementation will create an OrderPayment of
     * type CREDIT_CARD and save the passed in billing address
     *
     * @param request
     * @param response
     * @param model
     * @param paymentForm
     * @return the return path
     * @throws ServiceException
     */
    public String saveBillingAddress(HttpServletRequest request, HttpServletResponse response, Model model,
                                 PaymentInfoForm paymentForm, BindingResult result) throws PricingException, ServiceException {
        Order cart = CartState.getCart();

        preProcessBillingAddress(paymentForm, cart);
        paymentInfoFormValidator.validate(paymentForm, result);

        if (!result.hasErrors()) {
            if ((paymentForm.getAddress().getPhonePrimary() != null) &&
                    (StringUtils.isEmpty(paymentForm.getAddress().getPhonePrimary().getPhoneNumber()))) {
                paymentForm.getAddress().setPhonePrimary(null);
            }
            if ((paymentForm.getAddress().getPhoneSecondary() != null) &&
                    (StringUtils.isEmpty(paymentForm.getAddress().getPhoneSecondary().getPhoneNumber()))) {
                paymentForm.getAddress().setPhoneSecondary(null);
            }
            if ((paymentForm.getAddress().getPhoneFax() != null) &&
                    (StringUtils.isEmpty(paymentForm.getAddress().getPhoneFax().getPhoneNumber()))) {
                paymentForm.getAddress().setPhoneFax(null);
            }
            orderPaymentService.deleteOrderPaymentsByType(cart, PaymentType.CREDIT_CARD);

            addTemporaryOrderPayment(paymentForm, cart);

            cart.setEmailAddress(paymentForm.getEmailAddress());

            orderService.save(cart, true);
        }

        if (isAjaxRequest(request)) {
            //Add module specific model variables
            checkoutControllerExtensionManager.getProxy().addAdditionalModelVariables(model);
            return getCheckoutView();
        } else {
            return getCheckoutPageRedirect();
        }
    }

    protected void preProcessBillingAddress(PaymentInfoForm paymentForm, Order cart) {
        if (paymentForm.getShouldUseShippingAddress()){
            copyShippingAddressToBillingAddress(cart, paymentForm);
        }

        Boolean useCustomerPayment = paymentForm.getShouldUseCustomerPayment();
        if (useCustomerPayment && paymentForm.getCustomerPaymentId() != null) {
            copyCustomerPaymentAddressToBillingAddress(paymentForm);
        }

        addressService.populateAddressISOCountrySub(paymentForm.getAddress());
    }

    /**
     * This method will copy the shipping address of the first fulfillment group on the order
     * to the billing address on the PaymentInfoForm that is passed in.
     */
    protected void copyShippingAddressToBillingAddress(Order order, PaymentInfoForm paymentInfoForm) {
        if (order.getFulfillmentGroups().get(0) != null) {
            Address shipping = order.getFulfillmentGroups().get(0).getAddress();
            if (shipping != null) {
                Address billing = addressService.copyAddress(shipping) ;
                paymentInfoForm.setAddress(billing);
            }
        }
    }

    protected void copyCustomerPaymentAddressToBillingAddress(PaymentInfoForm paymentForm) {
        CustomerPayment customerPayment = customerPaymentService.readCustomerPaymentById(paymentForm.getCustomerPaymentId());

        if (customerPayment != null) {
            Address address = customerPayment.getBillingAddress();
            if (address != null) {
                paymentForm.setAddress(addressService.copyAddress(address));
            }
        }
    }

    protected void addTemporaryOrderPayment(PaymentInfoForm paymentForm, Order cart) {
        // A Temporary Order Payment will be created to hold the billing address.
        // The Payment Gateway will send back any validated address and
        // the PaymentGatewayCheckoutService will persist a new payment of type CREDIT_CARD when it applies it to the Order
        OrderPayment tempOrderPayment = orderPaymentService.create();
        tempOrderPayment.setType(PaymentType.CREDIT_CARD);
        tempOrderPayment.setPaymentGatewayType(PaymentGatewayType.TEMPORARY);
        tempOrderPayment.setBillingAddress(paymentForm.getAddress());
        tempOrderPayment.setOrder(cart);
        cart.getPayments().add(tempOrderPayment);
    }

}
