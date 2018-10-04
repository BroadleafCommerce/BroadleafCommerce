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
package org.broadleafcommerce.payment.service.workflow;

import java.util.Map;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.module.PaymentResponse;

public class PaymentSeed implements CompositePaymentResponse {

    private Map<PaymentInfo, Referenced> infos;
    private Order order;
    private PaymentResponse paymentResponse;

    public PaymentSeed(Order order, Map<PaymentInfo, Referenced> infos, PaymentResponse paymentResponse) {
        this.order = order;
        this.infos = infos;
        this.paymentResponse = paymentResponse;
    }

    public Map<PaymentInfo, Referenced> getInfos() {
        return infos;
    }

    public Order getOrder() {
        return order;
    }

    public PaymentResponse getPaymentResponse() {
        return paymentResponse;
    }

}
