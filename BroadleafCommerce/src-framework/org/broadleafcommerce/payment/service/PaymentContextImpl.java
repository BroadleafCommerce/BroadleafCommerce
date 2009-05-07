package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.util.money.Money;

public class PaymentContextImpl implements PaymentContext {

    protected Money originalPaymentAmount;
    protected Money remainingPaymentAmount;
    protected PaymentInfo paymentInfo;
    protected Referenced referencedPaymentInfo;

    public PaymentContextImpl(Money originalPaymentAmount, Money remainingPaymentAmount) {
        this.originalPaymentAmount = originalPaymentAmount;
        this.remainingPaymentAmount = remainingPaymentAmount;
    }

    public void setPaymentData(PaymentInfo paymentInfo, Referenced referencedPaymentInfo) {
        this.paymentInfo = paymentInfo;
        this.referencedPaymentInfo = referencedPaymentInfo;
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

    public void addPayment(Money paymentAmount) {
        remainingPaymentAmount = remainingPaymentAmount.subtract(paymentAmount);
        paymentInfo.setAmount(paymentAmount);
    }
}
