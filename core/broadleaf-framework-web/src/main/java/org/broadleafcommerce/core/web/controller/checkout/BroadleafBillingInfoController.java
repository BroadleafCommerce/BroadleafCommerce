/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
import org.broadleafcommerce.core.web.checkout.model.BillingInfoForm;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class BroadleafBillingInfoController extends AbstractCheckoutController {

    /**
     * Processes the request to save a billing address.
     *
     * Note: this default Broadleaf implementation will create an OrderPayment of
     * type CREDIT_CARD if it doesn't exist and save the passed in billing address
     *
     * @param request
     * @param response
     * @param model
     * @param billingForm
     * @return the return path
     * @throws org.broadleafcommerce.common.exception.ServiceException
     */
    public String saveBillingAddress(HttpServletRequest request, HttpServletResponse response, Model model,
                                 BillingInfoForm billingForm, BindingResult result) throws PricingException, ServiceException {
        Order cart = CartState.getCart();
        CustomerPayment customerPayment = null;

        if (billingForm.isUseShippingAddress()){
            copyShippingAddressToBillingAddress(cart, billingForm);
        }

        Boolean useCustomerPayment = billingForm.getUseCustomerPayment();
        if (useCustomerPayment && billingForm.getCustomerPaymentId() != null) {
            customerPayment = customerPaymentService.readCustomerPaymentById(billingForm.getCustomerPaymentId());

            if (customerPayment != null) {
                Address address = customerPayment.getBillingAddress();
                if (address != null) {
                    billingForm.setAddress(addressService.copyAddress(address));
                }
            }
        }

        addressService.populateAddressISOCountrySub(billingForm.getAddress());
        billingInfoFormValidator.validate(billingForm, result);
        if (result.hasErrors()) {
            return getCheckoutView();
        }

        if ((billingForm.getAddress().getPhonePrimary() != null) &&
                (StringUtils.isEmpty(billingForm.getAddress().getPhonePrimary().getPhoneNumber()))) {
            billingForm.getAddress().setPhonePrimary(null);
        }
        if ((billingForm.getAddress().getPhoneSecondary() != null) &&
                (StringUtils.isEmpty(billingForm.getAddress().getPhoneSecondary().getPhoneNumber()))) {
            billingForm.getAddress().setPhoneSecondary(null);
        }
        if ((billingForm.getAddress().getPhoneFax() != null) &&
                (StringUtils.isEmpty(billingForm.getAddress().getPhoneFax().getPhoneNumber()))) {
            billingForm.getAddress().setPhoneFax(null);
        }

        boolean found = false;
        String paymentName = billingForm.getPaymentName();
        Boolean saveNewPayment = billingForm.getSaveNewPayment();
        for (OrderPayment p : cart.getPayments()) {
            if (p.getType().isCreditCardType() && p.isActive()) {
                if (p.getBillingAddress() == null) {
                    p.setBillingAddress(billingForm.getAddress());
                } else {
                    Address updatedAddress = addressService.copyAddress(p.getBillingAddress(), billingForm.getAddress());
                    p.setBillingAddress(updatedAddress);
                }
                
                found = true;
            }
        }

        if (!found) {
            // A Temporary Order Payment will be created to hold the billing address.
            // The Payment Gateway will send back any validated address and
            // the PaymentGatewayCheckoutService will persist a new payment of type CREDIT_CARD when it applies it to the Order
            OrderPayment tempOrderPayment = orderPaymentService.create();
            tempOrderPayment.setType(PaymentType.CREDIT_CARD);
            tempOrderPayment.setPaymentGatewayType(PaymentGatewayType.TEMPORARY);
            tempOrderPayment.setBillingAddress(billingForm.getAddress());
            tempOrderPayment.setOrder(cart);
            cart.getPayments().add(tempOrderPayment);
        }

        orderService.save(cart, true);

        if (isAjaxRequest(request)) {
            //Add module specific model variables
            checkoutControllerExtensionManager.getProxy().addAdditionalModelVariables(model);
            return getCheckoutView();
        } else {
            return getCheckoutPageRedirect();
        }
    }

    /**
     * This method will copy the shipping address of the first fulfillment group on the order
     * to the billing address on the BillingInfoForm that is passed in.
     */
    protected void copyShippingAddressToBillingAddress(Order order, BillingInfoForm billingInfoForm) {
        if (order.getFulfillmentGroups().get(0) != null) {
            Address shipping = order.getFulfillmentGroups().get(0).getAddress();
            if (shipping != null) {
                Address billing = addressService.copyAddress(shipping) ;
                billingInfoForm.setAddress(billing);
            }
        }
    }

}
