package org.broadleafcommerce.payment.service.module;

import org.broadleafcommerce.payment.service.PaymentContext;
import org.broadleafcommerce.payment.service.exception.PaymentException;

public interface PaymentModule {

    public PaymentResponseItem authorize(PaymentContext paymentContext) throws PaymentException;

    public PaymentResponseItem debit(PaymentContext paymentContext) throws PaymentException;

    public PaymentResponseItem authorizeAndDebit(PaymentContext paymentContext) throws PaymentException;

    public PaymentResponseItem credit(PaymentContext paymentContext) throws PaymentException;

    public PaymentResponseItem voidPayment(PaymentContext paymentContext) throws PaymentException;

    public PaymentResponseItem balance(PaymentContext paymentContext) throws PaymentException;

    public Boolean isValidCandidate(String paymentType);

}
