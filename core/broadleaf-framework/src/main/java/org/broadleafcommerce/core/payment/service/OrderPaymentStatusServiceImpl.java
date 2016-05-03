/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.service.type.OrderPaymentStatus;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service("blOrderPaymentStatusService")
public class OrderPaymentStatusServiceImpl implements OrderPaymentStatusService {

    @Override
    public OrderPaymentStatus determineOrderPaymentStatus(OrderPayment orderPayment) {

        if (determineComplete(orderPayment)) {
            return OrderPaymentStatus.COMPLETE;
        } else if (determinePartiallyComplete(orderPayment)) {
            return OrderPaymentStatus.PARTIALLY_COMPLETE;
        } else if (determineFullyCaptured(orderPayment)) {
            return OrderPaymentStatus.FULLY_CAPTURED;
        } else if (determineAuthorized(orderPayment)) {
            return OrderPaymentStatus.AUTHORIZED;
        } else if (determinePending(orderPayment)) {
            return OrderPaymentStatus.PENDING;
        } else if (determineUnconfirmed(orderPayment)) {
            return OrderPaymentStatus.UNCONFIRMED;
        }

        return OrderPaymentStatus.UNDETERMINED;
    }

    protected boolean containsSuccessfulType(OrderPayment payment, PaymentTransactionType type) {
        List<PaymentTransaction> txs = payment.getTransactionsForType(type);
        for (PaymentTransaction tx : txs) {
            if (tx.getSuccess()) {
                return true;
            }
        }

        return false;
    }

    protected boolean determineComplete(OrderPayment payment) {
        List<PaymentTransaction> txs = new ArrayList<>();
        txs.addAll(payment.getTransactionsForType(PaymentTransactionType.REVERSE_AUTH));
        txs.addAll(payment.getTransactionsForType(PaymentTransactionType.DETACHED_CREDIT));
        for (PaymentTransaction tx : txs) {
            if (tx.getSuccess()) {
                return true;
            }
        }

        Money fullAuthAmount = payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE)
                .add(payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.AUTHORIZE));
        Money totalVoidAmount = payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.VOID);
        Money totalRefundAmount = payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.REFUND);

        return fullAuthAmount.greaterThan(Money.ZERO) &&
                (fullAuthAmount.equals(totalRefundAmount) || fullAuthAmount.equals(totalVoidAmount));
    }

    protected boolean determinePartiallyComplete(OrderPayment payment) {
        Money fullAuthAmount = payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.AUTHORIZE);
        Money fullCaptureAmount = payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.CAPTURE);
        Money totalVoidAmount = payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.VOID);
        Money totalRefundAmount = payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.REFUND);

        return !determineComplete(payment) &&
                ((totalRefundAmount.greaterThan(Money.ZERO) && fullAuthAmount.greaterThan(totalRefundAmount)) ||
                 (totalVoidAmount.greaterThan(Money.ZERO) && fullAuthAmount.greaterThan(totalVoidAmount)) ||
                        (containsSuccessfulType(payment, PaymentTransactionType.CAPTURE) &&
                         fullAuthAmount.greaterThan(Money.ZERO) &&
                         fullAuthAmount.greaterThan(fullCaptureAmount)));
    }

    protected boolean determineFullyCaptured(OrderPayment payment) {
        Money fullAuthAmount = payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.AUTHORIZE);
        Money fullCaptureAmount = payment.getSuccessfulTransactionAmountForType(PaymentTransactionType.CAPTURE);

        return containsSuccessfulType(payment, PaymentTransactionType.AUTHORIZE_AND_CAPTURE) ||
                (fullAuthAmount.greaterThan(Money.ZERO) && fullAuthAmount.equals(fullCaptureAmount));
    }

    protected boolean determineAuthorized(OrderPayment payment) {
        return !determineFullyCaptured(payment) && containsSuccessfulType(payment, PaymentTransactionType.AUTHORIZE);
    }

    protected boolean determinePending(OrderPayment payment) {
        return !determineAuthorized(payment) &&
                !containsSuccessfulType(payment, PaymentTransactionType.AUTHORIZE_AND_CAPTURE) &&
                containsSuccessfulType(payment, PaymentTransactionType.PENDING);
    }

    protected boolean determineUnconfirmed(OrderPayment payment) {
        return payment.getTransactions().size() == 1 &&
                payment.getTransactions().get(0).getSuccess() &&
                payment.getTransactions().get(0).getType().equals(PaymentTransactionType.UNCONFIRMED);
    }

}
