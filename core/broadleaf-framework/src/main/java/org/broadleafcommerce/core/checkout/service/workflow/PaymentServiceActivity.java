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
package org.broadleafcommerce.core.checkout.service.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.core.payment.service.CompositePaymentService;
import org.broadleafcommerce.core.payment.service.exception.InsufficientFundsException;
import org.broadleafcommerce.core.payment.service.workflow.CompositePaymentResponse;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map.Entry;

import javax.annotation.Resource;

public class PaymentServiceActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {
    
    private static final Log LOG = LogFactory.getLog(PaymentServiceActivity.class);

    @Resource(name="blCompositePaymentService")
    private CompositePaymentService compositePaymentService;
    
    @Value("${stop.checkout.on.single.payment.failure}")
    protected Boolean stopCheckoutOnSinglePaymentFailure;

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        CheckoutSeed seed = context.getSeedData();
        CompositePaymentResponse response = compositePaymentService.executePayment(seed.getOrder(), seed.getInfos(), seed.getPaymentResponse());
        
        for (Entry<PaymentInfo, PaymentResponseItem> entry : response.getPaymentResponse().getResponseItems().entrySet()) {
            checkTransactionStatus(context, entry.getValue());
            if (context.isStopped()) {
                String log = "Stopping checkout workflow due to payment response code: ";
                log += entry.getValue().getProcessorResponseCode();
                log += " and text: ";
                log += entry.getValue().getProcessorResponseText();
                log += " for payment type: " + entry.getKey().getType().getType();
                LOG.debug(log);
                break;
            }
        }

        // Validate that the total amount collected is not less than the order total
        Money paidAmount = new Money(0);
        for (Entry<PaymentInfo, PaymentResponseItem> entry : response.getPaymentResponse().getResponseItems().entrySet()) {
            if (entry.getValue().getTransactionSuccess()) {
                paidAmount = paidAmount.add(entry.getValue().getTransactionAmount());
            }
        }

        if (paidAmount.lessThan(seed.getOrder().getRemainingTotal())) {
            throw new InsufficientFundsException(String.format("Order remaining total was [%s] but paid amount was [%s]",
                    seed.getOrder().getTotal(), paidAmount));
        }

        return context;
    }
    
    protected void checkTransactionStatus(ProcessContext context, PaymentResponseItem paymentResponseItem) {
        if ((stopCheckoutOnSinglePaymentFailure != null && stopCheckoutOnSinglePaymentFailure) &&
                paymentResponseItem.getTransactionSuccess() != null && !paymentResponseItem.getTransactionSuccess()) {
            context.stopProcess();
        }
    }

}
