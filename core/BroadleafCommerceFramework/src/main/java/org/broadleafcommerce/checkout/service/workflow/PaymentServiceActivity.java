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
package org.broadleafcommerce.checkout.service.workflow;

import javax.annotation.Resource;

import org.broadleafcommerce.payment.service.CompositePaymentService;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class PaymentServiceActivity extends BaseActivity {

    @Resource(name="blCompositePaymentService")
    private CompositePaymentService compositePaymentService;

    public ProcessContext execute(ProcessContext context) throws Exception {
        CheckoutSeed seed = ((CheckoutContext) context).getSeedData();
        compositePaymentService.executePayment(seed.getOrder(), seed.getInfos(), seed.getPaymentResponse());

        return context;
    }

}
