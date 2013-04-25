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

package org.broadleafcommerce.core.checkout.service;

import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutResponse;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.workflow.SequenceProcessor;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service("blCheckoutService")
public class CheckoutServiceImpl implements CheckoutService {

    @Resource(name="blCheckoutWorkflow")
    protected SequenceProcessor checkoutWorkflow;

    @Resource(name="blOrderService")
    protected OrderService orderService;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.checkout.service.CheckoutService#performCheckout(org.broadleafcommerce.core.order.domain.Order, java.util.Map)
     */
    public CheckoutResponse performCheckout(Order order, final Map<PaymentInfo, Referenced> payments) throws CheckoutException {
        if (payments != null) {
            /*
             * TODO add validation that checks the order and payment information for validity.
             */
            /*
             * TODO remove this simple validation and encapsulate using our real validation strategy
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
        }

        CheckoutSeed seed = null;
        try {
            order = orderService.save(order, false);
            seed = new CheckoutSeed(order, payments, new HashMap<String, Object>());

            checkoutWorkflow.doActivities(seed);

            // We need to pull the order off the seed and save it here in case any activity modified the order.
            order = orderService.save(seed.getOrder(), false);
            seed.setOrder(order);

            return seed;
        } catch (PricingException e) {
            throw new CheckoutException("Unable to checkout order -- id: " + order.getId(), e, seed);
        } catch (WorkflowException e) {
            throw new CheckoutException("Unable to checkout order -- id: " + order.getId(), e.getRootCause(), seed);
        }
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.checkout.service.CheckoutService#performCheckout(org.broadleafcommerce.core.order.domain.Order)
     */
    public CheckoutResponse performCheckout(final Order order) throws CheckoutException {
        return performCheckout(order, null);
    }

}
