package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.exception.PaymentProcessorException;
import org.broadleafcommerce.payment.service.module.PaymentModule;
import org.broadleafcommerce.payment.service.module.PaymentResponse;

public class PaymentServiceImpl implements PaymentService {

    protected PaymentModule paymentModule;

    public PaymentModule getPaymentModule() {
        return paymentModule;
    }

    public void setPaymentModule(PaymentModule paymentModule) {
        this.paymentModule = paymentModule;
    }

    public PaymentResponse authorize(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException {
        return paymentModule.authorize(paymentContext);
    }

    public PaymentResponse authorizeAndDebit(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException {
        return paymentModule.authorizeAndDebit(paymentContext);
    }

    public PaymentResponse balance(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException {
        return paymentModule.balance(paymentContext);
    }

    public PaymentResponse credit(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException {
        return paymentModule.credit(paymentContext);
    }

    public PaymentResponse debit(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException {
        return paymentModule.debit(paymentContext);
    }

    public PaymentResponse voidPayment(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException {
        return paymentModule.voidPayment(paymentContext);
    }

    @Override
    public Boolean isValidCandidate(String paymentType) {
        return paymentModule.isValidCandidate(paymentType);
    }

}
