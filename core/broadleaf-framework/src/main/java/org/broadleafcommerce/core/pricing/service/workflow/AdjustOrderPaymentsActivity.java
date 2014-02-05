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
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
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
 * This algorithm will add up all the active applied payments to the order that are not of type
 * 'UNCONFIRMED' and payment type 'THIRD_PARTY_ACCOUNT'
 * The order.getTotal() minus all the applied payments that are NOT Unconfirmed and of a Third Party account
 * will then be set as the new amount that should be processed by the Third Party Account.
 *
 * Example:
 * 1) Initial Checkout Step
 * Order - Total = $30
 * - Order Payment (PayPal Express Checkout) - [Unconfirmed] $10
 * - Gift Card - [Unconfirmed] $10
 * - Customer Credit - [Unconfirmed] $10
 *
 * 2) Shipping Method picked and changes the order total
 * Order - Total = $35
 * - Order Payment (PayPal Express Checkout) - [Unconfirmed] $10
 * - Gift Card - [Unconfirmed] $10
 * - Customer Credit - [Unconfirmed] $10
 *
 * 3) Adjust Order Payment Activity ($35 - ($10 + $10)) = $15
 * Order - Total = $35
 * - Order Payment (PayPal Express Checkout) - [Unconfirmed] $15
 * - Gift Card - [Unconfirmed] $10
 * - Customer Credit - [Unconfirmed] $10
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class AdjustOrderPaymentsActivity extends BaseActivity<ProcessContext<Order>> {

    @Override
    public ProcessContext<Order> execute(ProcessContext<Order> context) throws Exception {
        Order order = context.getSeedData();

        OrderPayment unconfirmedThirdParty = null;
        Money appliedPaymentsWithoutThirdParty = Money.ZERO;
        for (OrderPayment payment : order.getPayments()) {
            if (payment.isActive()) {
                PaymentTransaction initialTransaction = payment.getInitialTransaction();

                if (initialTransaction != null &&
                        PaymentTransactionType.UNCONFIRMED.equals(initialTransaction.getType()) &&
                        PaymentType.THIRD_PARTY_ACCOUNT.equals(payment.getType()))  {
                    unconfirmedThirdParty = payment;
                } else if (payment.isActive() && payment.getAmount() != null) {
                    appliedPaymentsWithoutThirdParty.add(payment.getAmount());
                }
            }

        }

        if (unconfirmedThirdParty != null) {
            Money difference = order.getTotal().subtract(appliedPaymentsWithoutThirdParty);
            unconfirmedThirdParty.setAmount(difference);
        }

        context.setSeedData(order);
        return context;
    }

}
