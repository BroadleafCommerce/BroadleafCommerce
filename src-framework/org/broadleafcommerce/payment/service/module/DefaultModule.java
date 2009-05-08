package org.broadleafcommerce.payment.service.module;

import org.broadleafcommerce.payment.service.PaymentContext;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.exception.PaymentProcessorException;

public class DefaultModule implements PaymentModule {

    @Override
    public PaymentResponse authorize(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException {
        throw new PaymentException("authorize not implemented.");
    }

    @Override
    public PaymentResponse authorizeAndDebit(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException {
        throw new PaymentException("authorizeAndDebit not implemented.");
    }

    @Override
    public PaymentResponse debit(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException {
        throw new PaymentException("debit not implemented.");
    }

    @Override
    public PaymentResponse credit(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException {
        throw new PaymentException("credit not implemented.");
    }

    @Override
    public PaymentResponse voidPayment(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException {
        throw new PaymentException("voidPayment not implemented.");
    }

    @Override
    public PaymentResponse balance(PaymentContext paymentContext) throws PaymentException, PaymentProcessorException {
        throw new PaymentException("balance not implemented.");
    }

    @Override
    public Boolean isValidCandidate(String paymentType) {
        return false;
    }

}
