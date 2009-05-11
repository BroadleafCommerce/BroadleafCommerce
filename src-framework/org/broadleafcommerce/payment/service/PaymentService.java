package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.exception.PaymentProcessorException;
import org.broadleafcommerce.payment.service.module.PaymentResponseItem;

public interface PaymentService {

    public Boolean isValidCandidate(String paymentType);

    public PaymentResponseItem authorize(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException;

    public PaymentResponseItem debit(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException;

    public PaymentResponseItem authorizeAndDebit(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException;

    public PaymentResponseItem credit(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException;

    public PaymentResponseItem voidPayment(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException;

    public PaymentResponseItem balance(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException;

}
