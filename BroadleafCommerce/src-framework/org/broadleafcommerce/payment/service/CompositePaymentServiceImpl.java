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
package org.broadleafcommerce.payment.service;

import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.module.PaymentResponse;
import org.broadleafcommerce.payment.service.module.PaymentResponseImpl;
import org.broadleafcommerce.payment.service.workflow.CompositePaymentResponse;
import org.broadleafcommerce.payment.service.workflow.PaymentSeed;
import org.broadleafcommerce.workflow.SequenceProcessor;
import org.broadleafcommerce.workflow.WorkflowException;
import org.springframework.stereotype.Service;

/**
 * Execute the payment workflow independently of the checkout workflow
 * @author jfischer
 */
@Service("blCompositePaymentService")
public class CompositePaymentServiceImpl implements CompositePaymentService {

    @Resource(name = "blPaymentWorkflow")
    protected SequenceProcessor paymentWorkflow;

    public CompositePaymentResponse executePayment(Order order, Map<PaymentInfo, Referenced> payments, PaymentResponse response) throws PaymentException {
        /*
         * TODO add validation that checks the order and payment information for
         * validity.
         */
        try {
            PaymentSeed seed = new PaymentSeed(order, payments, response);
            paymentWorkflow.doActivities(seed);

            return seed;
        } catch (WorkflowException e) {
            Throwable cause = null;
            while (e.getCause() != null) {
                if (cause != null && cause.equals(e.getCause())) {
                    break;
                }
                cause = e.getCause();
            }
            if (cause != null && PaymentException.class.isAssignableFrom(cause.getClass())) {
                throw (PaymentException) cause;
            }
            throw new PaymentException("Unable to execute payment for order -- id: " + order.getId(), e);
        }
    }

    public CompositePaymentResponse executePayment(Order order, Map<PaymentInfo, Referenced> payments) throws PaymentException {
        return executePayment(order, payments, new PaymentResponseImpl());
    }

    public CompositePaymentResponse executePayment(Order order) throws PaymentException {
        return executePayment(order, null);
    }

}
