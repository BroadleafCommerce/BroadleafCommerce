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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.secure.Referenced;
import org.broadleafcommerce.core.payment.service.type.PaymentType;
import org.broadleafcommerce.core.web.checkout.model.BillingInfoForm;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

/**
 * Extension handler for the OrderPayment services
 *
 * @author Joshua Skorton (jskorton)
 */
public interface OrderPaymentServiceExtensionHandler extends ExtensionHandler {

    /**
     * Adds additional PaymentInfos to the payments map which will be used to checkout.  An implementing extension handler should 
     * check the paymentInfoTypeList to confirm that it should execute.
     *  
     * @param payments
     * @param paymentInfoTypeList - If a PaymentType is included in this list, the corresponding extension handler should execute.
     * @param request
     * @param response
     * @param model
     * @param billingForm
     * @param result
     * @return
     */
    public ExtensionResultStatusType addAdditionalPaymentInfos(Map<OrderPayment, Referenced> payments, List<PaymentType> paymentInfoTypeList, HttpServletRequest request, HttpServletResponse response, Model model, BillingInfoForm billingForm, BindingResult result);
}
