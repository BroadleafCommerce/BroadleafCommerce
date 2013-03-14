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

package org.broadleafcommerce.core.workflow;

import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.core.payment.service.workflow.PaymentSeed;
import java.util.Map;

/**
 * Abstract class used to provide payment related activities (payment, refund, void, etc.) general
 * methods used when communication with payment providers.
 *
 * @author Jerry Ocanas (jocanas)
 *
 */
public abstract class PaymentModuleActivity extends BaseActivity {

    protected static final String MESSAGE = "MESSAGE";

    protected void validateResponse(PaymentSeed paymentSeed) throws Exception {
        boolean success = false;
        String message = null;

        for (Map.Entry<PaymentInfo, PaymentResponseItem> entry : paymentSeed.getPaymentResponse().getResponseItems().entrySet()) {
            if (entry.getValue().getTransactionSuccess()) {
                success = true;
            } else {
                message = entry.getValue().getAdditionalFields().get(MESSAGE);
            }
        }
        if (!success) {
            throw new Exception(message);
        }
    }
}
