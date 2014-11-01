/*
 * #%L
 * BroadleafCommerce Framework Web
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

        if (billingForm.isUseShippingAddress()){
            copyShippingAddressToBillingAddress(cart, billingForm);
        }

        billingInfoFormValidator.validate(billingForm, result);
        if (result.hasErrors()) {
            return getCheckoutView();
        }

        if ((billingForm.getAddress().getPhonePrimary() != null) &&
                (StringUtils.isEmpty(billingForm.getAddress().getPhonePrimary().getPhoneNumber()))) {
            billingForm.getAddress().setPhonePrimary(null);
        }

        boolean found = false;
        for (OrderPayment p : cart.getPayments()) {
            if (PaymentType.CREDIT_CARD.equals(p.getType()) &&
                    p.isActive()) {
                p.setBillingAddress(billingForm.getAddress());
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
                Address billing = addressService.create();
                billing.setFirstName(shipping.getFirstName());
                billing.setLastName(shipping.getLastName());
                billing.setAddressLine1(shipping.getAddressLine1());
                billing.setAddressLine2(shipping.getAddressLine2());
                billing.setCity(shipping.getCity());
                billing.setState(shipping.getState());
                billing.setPostalCode(shipping.getPostalCode());
                billing.setCountry(shipping.getCountry());
                billing.setPrimaryPhone(shipping.getPrimaryPhone());
                billing.setEmailAddress(shipping.getEmailAddress());
                billingInfoForm.setAddress(billing);
            }
        }
    }


}
