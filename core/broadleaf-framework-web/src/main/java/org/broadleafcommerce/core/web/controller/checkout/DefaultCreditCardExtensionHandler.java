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

package org.broadleafcommerce.core.web.controller.checkout;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.PaymentInfoFactory;
import org.broadleafcommerce.core.payment.service.SecurePaymentInfoService;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.core.web.checkout.model.BillingInfoForm;
import org.broadleafcommerce.core.web.checkout.validator.BillingInfoFormValidator;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Extension handler to add a credit card PaymentInfo to the payments map and the order
 *
 * @author Joshua Skorton (jskorton)
 */
@Service("blDefaultCreditCardExtensionHandler")
public class DefaultCreditCardExtensionHandler extends AbstractExtensionHandler implements PaymentInfoServiceExtensionHandler {

    @Resource(name = "blPaymentInfoServiceExtensionManager")
    protected PaymentInfoServiceExtensionManager extensionManager;

    @Resource(name = "blCreditCardPaymentInfoFactory")
    protected PaymentInfoFactory paymentInfoFactory;

    @Resource(name = "blSecurePaymentInfoService")
    protected SecurePaymentInfoService securePaymentInfoService;

    @Resource(name = "blBillingInfoFormValidator")
    protected BillingInfoFormValidator billingInfoFormValidator;

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    /**
     * Adds a credit card PaymentInfo to the payments map and the order.  The paymentInfoTypeList is checked to confirm 
     * that the credit card PaymentInfo should be added.
     *  
     * @param payments
     * @param paymentInfoTypeList - Checks this list to decide if credit card PaymentInfos should be added to the payments map.
     * @param request
     * @param response
     * @param model
     * @param billingForm
     * @param result
     * @return
     */
    @Override
    public ExtensionResultStatusType addAdditionalPaymentInfos(Map<PaymentInfo, Referenced> payments, List<PaymentInfoType> paymentInfoTypeList, HttpServletRequest request, HttpServletResponse response, Model model, BillingInfoForm billingForm, BindingResult result) {
        for (PaymentInfoType paymentInfoType : paymentInfoTypeList) {
            if (PaymentInfoType.CREDIT_CARD.equals(paymentInfoType)) {
                return addToPaymentsMap(payments, billingForm, result);
            }
        }
        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    /**
     * Adds a credit card PaymentInfo to the payments map and the order.
     * 
     * @param payments
     * @param billingForm
     * @param result
     * @return
     */
    protected ExtensionResultStatusType addToPaymentsMap(Map<PaymentInfo, Referenced> payments, BillingInfoForm billingForm, BindingResult result) {
        Order cart = CartState.getCart();
        if (billingForm.isUseShippingAddress()) {
            copyShippingAddressToBillingAddress(cart, billingForm);
        }

        billingInfoFormValidator.validate(billingForm, result);
        if (result.hasErrors()) {
            return ExtensionResultStatusType.HANDLED_STOP;
        }

        PaymentInfo ccInfo = paymentInfoFactory.constructPaymentInfo(cart);
        ccInfo.setAddress(billingForm.getAddress());
        cart.getPaymentInfos().add(ccInfo);
    
        CreditCardPaymentInfo ccReference = (CreditCardPaymentInfo) securePaymentInfoService.create(PaymentInfoType.CREDIT_CARD);
        ccReference.setNameOnCard(billingForm.getCreditCardName());
        ccReference.setReferenceNumber(ccInfo.getReferenceNumber());
        ccReference.setPan(billingForm.getCreditCardNumber());
        ccReference.setCvvCode(billingForm.getCreditCardCvvCode());
        ccReference.setExpirationMonth(Integer.parseInt(billingForm.getCreditCardExpMonth()));
        ccReference.setExpirationYear(Integer.parseInt(billingForm.getCreditCardExpYear()));

        payments.put(ccInfo, ccReference);

        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }
    
    /**
     * This method will copy the shipping address of the first shippable fulfillment group on the order
     * to the billing address on the BillingInfoForm that is passed in.
     *
     * @param billingInfoForm
     */
    protected void copyShippingAddressToBillingAddress(Order order, BillingInfoForm billingInfoForm) {
        FulfillmentGroup fulfillmentGroup = fulfillmentGroupService.getFirstShippableFulfillmentGroup(order);
        if (fulfillmentGroup != null) {
            Address shipping = fulfillmentGroup.getAddress();
            if (shipping != null) {
                Address billing = new AddressImpl();
                billing.setFirstName(shipping.getFirstName());
                billing.setLastName(shipping.getLastName());
                billing.setAddressLine1(shipping.getAddressLine1());
                billing.setAddressLine2(shipping.getAddressLine2());
                billing.setCity(shipping.getCity());
                billing.setState(shipping.getState());
                billing.setPostalCode(shipping.getPostalCode());
                billing.setCountry(shipping.getCountry());
                billing.setPhonePrimary(shipping.getPhonePrimary());
                billing.setEmailAddress(shipping.getEmailAddress());
                billingInfoForm.setAddress(billing);
            }
        }
    }

}
