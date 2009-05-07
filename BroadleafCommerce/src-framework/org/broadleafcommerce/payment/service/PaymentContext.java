package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.util.money.Money;

public interface PaymentContext {

    public Money getOriginalPaymentAmount();

    public Money getRemainingPaymentAmount();

    public PaymentInfo getPaymentInfo();

    public Referenced getReferencedPaymentInfo();

    public void addPayment(Money paymentAmount);

}
