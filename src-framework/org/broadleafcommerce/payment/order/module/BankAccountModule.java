package org.broadleafcommerce.payment.order.module;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.order.exception.PaymentException;
import org.broadleafcommerce.payment.secure.domain.BankAccountPaymentInfo;

public interface BankAccountModule {

    public String getName();

    public void setName(String name);

    public void authorize(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException;

    public void debit(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException;

    public void authorizeAndDebit(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException;

}
