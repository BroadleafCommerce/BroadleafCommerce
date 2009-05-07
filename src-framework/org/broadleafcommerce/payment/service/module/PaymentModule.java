package org.broadleafcommerce.payment.service.module;

import org.broadleafcommerce.payment.service.PaymentContext;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.exception.PaymentProcessorException;

public interface PaymentModule {

    public String getName();

    public void setName(String name);

    public PaymentResponse authorize(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException;

    public PaymentResponse debit(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException;

    public PaymentResponse authorizeAndDebit(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException;

    public PaymentResponse credit(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException;

    public PaymentResponse voidPayment(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException;

    public PaymentResponse balance(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException;

    public Boolean isValidCandidate(String paymentType);

}
