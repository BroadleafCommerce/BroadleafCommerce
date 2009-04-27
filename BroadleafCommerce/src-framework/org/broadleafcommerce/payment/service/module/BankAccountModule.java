package org.broadleafcommerce.payment.service.module;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.service.exception.PaymentException;

public interface BankAccountModule {

    public String getName();

    public void setName(String name);

    public void authorize(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException;

    public void debit(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException;

    public void authorizeAndDebit(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException;

}
