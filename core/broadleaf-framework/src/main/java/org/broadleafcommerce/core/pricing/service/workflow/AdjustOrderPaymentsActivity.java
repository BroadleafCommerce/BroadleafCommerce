/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.pricing.service.workflow;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.math.BigDecimal;

/**
 * The AdjustOrderPaymentsActivity is responsible for adjusting any of the order payments
 * that have already been applied to the order. This happens when order payments have
 * been applied to the order but the order total has changed. In the case of a hosted
 * gateway solution like PayPal Express Checkout, the order payment is created when the
 * customer redirects to the Review Order Page (Checkout page) and the user selects
 * a shipping method which may affect the order total. Since the Hosted Order payment
 * is unconfirmed, we need to adjust the amount on this order payment before
 * we complete checkout and confirm the payment with PayPal again.
 *
 * For this default implementation,
 * the remaining difference is added to the the Order Payment of PaymentType.THIRD_PARTY_ACCOUNT.
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class AdjustOrderPaymentsActivity extends BaseActivity<ProcessContext<Order>> {

    @Override
    public ProcessContext<Order> execute(ProcessContext<Order> context) throws Exception {
        Order order = context.getSeedData();

        Money difference = new Money(BigDecimal.ZERO);
        // Add authorize and authorize_and_capture; there should only be one or the other in the payment
        Money paymentSum = new Money(BigDecimal.ZERO);
        for (OrderPayment payment : order.getPayments()) {
            paymentSum = paymentSum.add(payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.AUTHORIZE))
                    .add(payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE));
        }

        if (!paymentSum.equals(order.getTotal())) {
            difference = order.getTotal().subtract(paymentSum);
            for (OrderPayment payment: order.getPayments()) {
                if (PaymentType.THIRD_PARTY_ACCOUNT.equals(payment.getType())) {
                    payment.setAmount(payment.getAmount().add(difference));
                }
            }
        }

        context.setSeedData(order);
        return context;
    }

}
