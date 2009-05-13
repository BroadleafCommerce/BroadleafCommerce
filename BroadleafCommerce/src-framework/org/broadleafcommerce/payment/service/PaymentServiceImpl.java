package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.module.PaymentModule;
import org.broadleafcommerce.payment.service.module.PaymentResponseItem;

public class PaymentServiceImpl implements PaymentService {

    protected PaymentModule paymentModule;

    public PaymentModule getPaymentModule() {
        return paymentModule;
    }

    public void setPaymentModule(PaymentModule paymentModule) {
        this.paymentModule = paymentModule;
    }

    public PaymentResponseItem authorize(PaymentContext paymentContext) throws PaymentException  {
        return paymentModule.authorize(paymentContext);
    }

    public PaymentResponseItem authorizeAndDebit(PaymentContext paymentContext) throws PaymentException {
        return paymentModule.authorizeAndDebit(paymentContext);
    }

    public PaymentResponseItem balance(PaymentContext paymentContext) throws PaymentException {
        return paymentModule.balance(paymentContext);
    }

    public PaymentResponseItem credit(PaymentContext paymentContext) throws PaymentException {
        return paymentModule.credit(paymentContext);
    }

    public PaymentResponseItem debit(PaymentContext paymentContext) throws PaymentException {
        return paymentModule.debit(paymentContext);
    }

    public PaymentResponseItem voidPayment(PaymentContext paymentContext) throws PaymentException {
        return paymentModule.voidPayment(paymentContext);
    }

    @Override
    public Boolean isValidCandidate(String paymentType) {
        return paymentModule.isValidCandidate(paymentType);
    }

}
