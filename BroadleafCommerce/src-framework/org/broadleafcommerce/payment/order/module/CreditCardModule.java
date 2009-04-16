package org.broadleafcommerce.payment.order.module;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.order.exception.PaymentException;
import org.broadleafcommerce.payment.secure.domain.CreditCardPaymentInfo;

public interface CreditCardModule {

    public String getName();

    public void setName(String name);

    public void authorize(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException;

    public void debit(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException;

    public void authorizeAndDebit(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException;

}
