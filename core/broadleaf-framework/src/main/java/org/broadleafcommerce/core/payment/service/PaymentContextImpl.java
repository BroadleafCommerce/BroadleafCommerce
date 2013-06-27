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

package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.Referenced;

public class PaymentContextImpl implements PaymentContext {

    protected Money originalPaymentAmount;
    protected Money remainingPaymentAmount;
    protected Money transactionAmount;
    protected Money remainingTransactionAmount;
    protected PaymentInfo paymentInfo;
    protected Referenced referencedPaymentInfo;
    protected String transactionId;
    protected String userName;

    public PaymentContextImpl(Money transactionAmount, Money remainingTransactionAmount, PaymentInfo paymentInfo, Referenced referencedPaymentInfo, String userName) {
        this.transactionAmount = transactionAmount;
        this.remainingTransactionAmount = remainingTransactionAmount;
        this.paymentInfo = paymentInfo;
        this.referencedPaymentInfo = referencedPaymentInfo;
        this.userName = userName;
    }

    @Deprecated
    public Money getOriginalPaymentAmount() {
        return originalPaymentAmount;
    }

    @Deprecated
    public Money getRemainingPaymentAmount() {
        return remainingPaymentAmount;
    }

    @Override
    public Money getTransactionAmount() {
        return transactionAmount;
    }

    @Override
    public void setTransactionAmount(Money transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    @Override
    public Money getRemainingTransactionAmount() {
        return remainingTransactionAmount;
    }

    @Override
    public void setRemainingTransactionAmount(Money remainingTransactionAmount) {
        this.remainingTransactionAmount = remainingTransactionAmount;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public Referenced getReferencedPaymentInfo() {
        return referencedPaymentInfo;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserName() {
        return userName;
    }

}
