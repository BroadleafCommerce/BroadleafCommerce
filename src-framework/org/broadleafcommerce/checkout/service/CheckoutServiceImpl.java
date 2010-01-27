/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.checkout.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.checkout.service.workflow.CheckoutResponse;
import org.broadleafcommerce.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.pricing.service.exception.PricingException;
import org.broadleafcommerce.time.SystemTime;
import org.broadleafcommerce.workflow.SequenceProcessor;
import org.broadleafcommerce.workflow.WorkflowException;
import org.springframework.stereotype.Service;

@Service("blCheckoutService")
public class CheckoutServiceImpl implements CheckoutService {

    @Resource(name = "blCheckoutWorkflow")
    protected SequenceProcessor checkoutWorkflow;

    @Resource(name = "blCartService")
    protected CartService cartService;

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.checkout.service.CheckoutService#performCheckout
     * (org.broadleafcommerce.order.domain.Order, java.util.Map)
     */
    public CheckoutResponse performCheckout(Order order, Map<PaymentInfo, Referenced> payments) throws CheckoutException {
        /*
         * TODO add validation that checks the order and payment information for
         * validity.
         */
        /*
         * TODO remove this simple validation and encapsulate using our real
         * validation strategy
         */
        for (PaymentInfo info : payments.keySet()) {
            if (info.getReferenceNumber() == null) {
                throw new CheckoutException("PaymentInfo reference number cannot be null", null);
            }
        }
        for (Referenced referenced : payments.values()) {
            if (referenced.getReferenceNumber() == null) {
                throw new CheckoutException("Referenced reference number cannot be null", null);
            }
        }

        CheckoutSeed seed = null;
        try {
            order.setSubmitDate(SystemTime.asDate());
            order = cartService.save(order, false);

            seed = new CheckoutSeed(order, payments, new HashMap<String, Object>());
            checkoutWorkflow.doActivities(seed);

            return seed;
        } catch (PricingException e) {
            throw new CheckoutException("Unable to checkout order -- id: " + order.getId(), e, seed);
        } catch (WorkflowException e) {
            Throwable cause = e;
            while (e.getCause() != null) {
                if (cause.equals(e.getCause())) {
                    break;
                }
                cause = e.getCause();
            }
            throw new CheckoutException("Unable to checkout order -- id: " + order.getId(), cause, seed);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.checkout.service.CheckoutService#performCheckout
     * (org.broadleafcommerce.order.domain.Order)
     */
    public CheckoutResponse performCheckout(Order order) throws CheckoutException {
        return performCheckout(order, null);
    }
}
