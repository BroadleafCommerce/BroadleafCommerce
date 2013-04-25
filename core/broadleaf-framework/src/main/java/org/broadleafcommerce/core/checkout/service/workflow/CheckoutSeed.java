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

package org.broadleafcommerce.core.checkout.service.workflow;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.module.PaymentResponse;
import org.broadleafcommerce.core.payment.service.module.PaymentResponseImpl;

import java.util.Map;

public class CheckoutSeed implements CheckoutResponse {

    private Map<PaymentInfo, Referenced> infos;
    private Order order;
    private PaymentResponse paymentResponse = new PaymentResponseImpl();
    private Map<String, Object> userDefinedFields;

    public CheckoutSeed(Order order, Map<PaymentInfo, Referenced> infos, Map<String, Object> userDefinedFields) {
        this.order = order;
        this.infos = infos;
        this.userDefinedFields = userDefinedFields;
    }

    public Map<PaymentInfo, Referenced> getInfos() {
        return infos;
    }

    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }

    public PaymentResponse getPaymentResponse() {
        return paymentResponse;
    }

    public Map<String, Object> getUserDefinedFields() {
        return userDefinedFields;
    }
}
