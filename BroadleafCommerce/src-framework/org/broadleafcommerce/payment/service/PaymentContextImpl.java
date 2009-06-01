package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.util.money.Money;

public class PaymentContextImpl implements PaymentContext {

    protected Money originalPaymentAmount;
    protected Money remainingPaymentAmount;
    protected PaymentInfo paymentInfo;
    protected Referenced referencedPaymentInfo;
    protected String transactionId;
    protected String userName;

    public PaymentContextImpl(Money originalPaymentAmount, Money remainingPaymentAmount, PaymentInfo paymentInfo, Referenced referencedPaymentInfo, String userName) {
        this.originalPaymentAmount = originalPaymentAmount;
        this.remainingPaymentAmount = remainingPaymentAmount;
        this.paymentInfo = paymentInfo;
        this.referencedPaymentInfo = referencedPaymentInfo;
        this.userName = userName;
    }

    public Money getOriginalPaymentAmount() {
        return originalPaymentAmount;
    }

    public Money getRemainingPaymentAmount() {
        return remainingPaymentAmount;
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
