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
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.CODPaymentInfoFactoryImpl;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.core.web.checkout.model.BillingInfoForm;
import org.broadleafcommerce.core.web.order.CartState;
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
 * Extension handler to add a Collect On Delivery(COD) PaymentInfo to the payments map and to the order
 *
 * @author Joshua Skorton (jskorton)
 */
@Service("blDefaultCODExtensionHandler")
public class DefaultCODExtensionHandler extends AbstractExtensionHandler implements PaymentInfoServiceExtensionHandler {

    @Resource(name = "blPaymentInfoServiceExtensionManager")
    protected PaymentInfoServiceExtensionManager extensionManager;

    @Resource(name = "blCODPaymentInfoFactory")
    protected CODPaymentInfoFactoryImpl codPaymentInfoFactory;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.getHandlers().add(this);
        }
    }

    /**
     * Adds a COD PaymentInfo to the payments map and the order.  The paymentInfoTypeList is checked to confirm 
     * that the COD PaymentInfo should be added.
     *  
     * @param payments
     * @param paymentInfoTypeList - Checks this list to decide if COD PaymentInfos should be added to the payments map.
     * @param request
     * @param response
     * @param model
     * @param billingForm
     * @param result
     * @return
     */
    @Override
    public ExtensionResultStatusType addAdditionalPaymentInfos(Map<PaymentInfo, Referenced> payments, List<PaymentInfoType> paymentInfoTypeList, HttpServletRequest request, HttpServletResponse response, Model model, BillingInfoForm billingForm, BindingResult result) {
        for(PaymentInfoType paymentInfoType : paymentInfoTypeList) {
            if (PaymentInfoType.COD.equals(paymentInfoType)) {
                return addToPaymentsMap(payments);
            }
        }
        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    /**
     * Adds a COD PaymentInfo to the payments map and the order.
     * 
     * @param payments
     * @return
     */
    protected ExtensionResultStatusType addToPaymentsMap(Map<PaymentInfo, Referenced> payments) {
        Order cart = CartState.getCart();
        PaymentInfo codPaymentInfo = codPaymentInfoFactory.constructPaymentInfo(cart);
        cart.getPaymentInfos().add(codPaymentInfo);
        payments.put(codPaymentInfo, codPaymentInfo.createEmptyReferenced());
        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }
    
}
