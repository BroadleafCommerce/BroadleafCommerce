package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.util.money.Money;

public class PaymentContextImpl implements PaymentContext {

    protected Money originalPaymentAmount;
    protected Money remainingPaymentAmount;
    protected PaymentInfo paymentInfo;
    protected Referenced referencedPaymentInfo;

    public PaymentContextImpl(Money originalPaymentAmount, Money remainingPaymentAmount, PaymentInfo paymentInfo, Referenced referencedPaymentInfo) {
        this.originalPaymentAmount = originalPaymentAmount;
        this.remainingPaymentAmount = remainingPaymentAmount;
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

}
