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

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.exception.PaymentException;
import org.broadleafcommerce.core.payment.service.module.PaymentResponse;
import org.broadleafcommerce.core.payment.service.module.PaymentResponseImpl;
import org.broadleafcommerce.core.payment.service.workflow.CompositePaymentResponse;
import org.broadleafcommerce.core.payment.service.workflow.PaymentSeed;
import org.broadleafcommerce.core.workflow.SequenceProcessor;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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

    //This convenience method is utilized for those implementations that are not storing secure information (credit card information), such as PayPal and Braintree
    //It will construct a PaymentInfo based on the implementation of PaymentInfoFactory with an empty Referenced and pass it to the workflow.
    public CompositePaymentResponse executePaymentForGateway(Order order, PaymentInfoFactory paymentInfoFactory) throws PaymentException {
        Map<PaymentInfo, Referenced> payments = new HashMap<PaymentInfo, Referenced>();
        PaymentInfo paymentInfo = paymentInfoFactory.constructPaymentInfo(order);
        payments.put(paymentInfo, paymentInfo.createEmptyReferenced());

        order.getPaymentInfos().add(paymentInfo);

        return executePayment(order, payments);
    }

    public SequenceProcessor getPaymentWorkflow() {
        return paymentWorkflow;
    }

    public void setPaymentWorkflow(SequenceProcessor paymentWorkflow) {
        this.paymentWorkflow = paymentWorkflow;
    }
}
