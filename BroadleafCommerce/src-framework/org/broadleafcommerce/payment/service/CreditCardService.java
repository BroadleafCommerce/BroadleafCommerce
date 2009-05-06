package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.exception.PaymentProcessorException;
import org.broadleafcommerce.payment.service.module.PaymentResponse;

public interface CreditCardService {

    public PaymentResponse authorize(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException, PaymentProcessorException;

    public PaymentResponse debit(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException, PaymentProcessorException;

    public PaymentResponse authorizeAndDebit(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException, PaymentProcessorException;

    public PaymentResponse credit(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException, PaymentProcessorException;

    public PaymentResponse voidPayment(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException, PaymentProcessorException;

}
