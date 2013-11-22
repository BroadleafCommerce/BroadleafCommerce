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
package org.broadleafcommerce.core.payment.service.workflow;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;
import org.broadleafcommerce.core.payment.service.module.PaymentResponse;

import java.util.Map;

public class PaymentSeed implements CompositePaymentResponse {

    private Order order;
    private Map<PaymentInfo, Referenced> infos;
    private PaymentResponse paymentResponse;
    private Money transactionAmount;

    public PaymentSeed(Order order, Map<PaymentInfo, Referenced> infos, PaymentResponse paymentResponse) {
        this.order = order;
        this.infos = infos;
        this.paymentResponse = paymentResponse;
    }

    public PaymentSeed(Order order, Map<PaymentInfo, Referenced> infos, PaymentResponse paymentResponse, Money transactionAmount) {
        this.infos = infos;
        this.order = order;
        this.paymentResponse = paymentResponse;
        this.transactionAmount = transactionAmount;
    }

    public Order getOrder() {
        return order;
    }

    public Map<PaymentInfo, Referenced> getInfos() {
        return infos;
    }

    public PaymentResponse getPaymentResponse() {
        return paymentResponse;
    }

    public Money getTransactionAmount() {
        return transactionAmount;
    }

}
