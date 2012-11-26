/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.core.checkout.service.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.core.payment.service.CompositePaymentService;
import org.broadleafcommerce.core.payment.service.workflow.CompositePaymentResponse;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

import java.util.Map;

public class PaymentServiceActivity extends BaseActivity {
    
    private static final Log LOG = LogFactory.getLog(PaymentServiceActivity.class);

    @Resource(name="blCompositePaymentService")
    private CompositePaymentService compositePaymentService;
    
    @Value("${stop.checkout.on.single.payment.failure}")
    protected String stopCheckoutOnSinglePaymentFailure;

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        CheckoutSeed seed = ((CheckoutContext) context).getSeedData();
        CompositePaymentResponse response = compositePaymentService.executePayment(seed.getOrder(), seed.getInfos(), seed.getPaymentResponse());
        
        for (Map.Entry<PaymentInfo, PaymentResponseItem> entry : response.getPaymentResponse().getResponseItems().entrySet()) {
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

        return context;
    }
    
    protected void checkTransactionStatus(ProcessContext context, PaymentResponseItem paymentResponseItem) {
        if ("true".equalsIgnoreCase(stopCheckoutOnSinglePaymentFailure) &&
                paymentResponseItem.getTransactionSuccess() != null && !paymentResponseItem.getTransactionSuccess()) {
            context.stopProcess();
        }
    }

}
