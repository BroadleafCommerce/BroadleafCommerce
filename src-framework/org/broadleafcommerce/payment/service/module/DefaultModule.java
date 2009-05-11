package org.broadleafcommerce.payment.service.module;

import org.broadleafcommerce.payment.service.PaymentContext;
import org.broadleafcommerce.payment.service.exception.PaymentException;

public class DefaultModule implements PaymentModule {

    @Override
    public PaymentResponseItem authorize(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("authorize not implemented.");
    }

    @Override
    public PaymentResponseItem authorizeAndDebit(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("authorizeAndDebit not implemented.");
    }

    @Override
    public PaymentResponseItem debit(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("debit not implemented.");
    }

    @Override
    public PaymentResponseItem credit(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("credit not implemented.");
    }

    @Override
    public PaymentResponseItem voidPayment(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("voidPayment not implemented.");
    }

    @Override
    public PaymentResponseItem balance(PaymentContext paymentContext) throws PaymentException {
        throw new PaymentException("balance not implemented.");
    }

    @Override
    public Boolean isValidCandidate(String paymentType) {
        return false;
    }

}
