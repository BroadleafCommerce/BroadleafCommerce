package org.broadleafcommerce.payment.service.module;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.payment.service.exception.PaymentException;

public interface CreditCardModule {

    public String getName();

    public void setName(String name);

    public void authorize(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException;

    public void debit(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException;

    public void authorizeAndDebit(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException;

}
