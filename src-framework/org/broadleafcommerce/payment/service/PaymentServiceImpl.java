package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.module.PaymentModule;
import org.broadleafcommerce.payment.service.module.PaymentResponseItem;
import org.broadleafcommerce.payment.service.type.BLCTransactionType;

public class PaymentServiceImpl implements PaymentService {

    protected PaymentModule paymentModule;

    public PaymentModule getPaymentModule() {
        return paymentModule;
    }

    public void setPaymentModule(PaymentModule paymentModule) {
        this.paymentModule = paymentModule;
    }

    public PaymentResponseItem authorize(PaymentContext paymentContext) throws PaymentException  {
        PaymentResponseItem response = paymentModule.authorize(paymentContext);
        response.setTransactionType(BLCTransactionType.AUTHORIZE);
        return response;
    }

    public PaymentResponseItem authorizeAndDebit(PaymentContext paymentContext) throws PaymentException {
        PaymentResponseItem response = paymentModule.authorizeAndDebit(paymentContext);
        response.setTransactionType(BLCTransactionType.AUTHORIZEANDDEBIT);
        return response;
    }

    public PaymentResponseItem balance(PaymentContext paymentContext) throws PaymentException {
        PaymentResponseItem response = paymentModule.balance(paymentContext);
        response.setTransactionType(BLCTransactionType.BALANCE);
        return response;
    }

    public PaymentResponseItem credit(PaymentContext paymentContext) throws PaymentException {
        PaymentResponseItem response = paymentModule.credit(paymentContext);
        response.setTransactionType(BLCTransactionType.CREDIT);
        return response;
    }

    public PaymentResponseItem debit(PaymentContext paymentContext) throws PaymentException {
        PaymentResponseItem response = paymentModule.debit(paymentContext);
        response.setTransactionType(BLCTransactionType.DEBIT);
        return response;
    }

    public PaymentResponseItem voidPayment(PaymentContext paymentContext) throws PaymentException {
        PaymentResponseItem response = paymentModule.voidPayment(paymentContext);
        response.setTransactionType(BLCTransactionType.VOIDPAYMENT);
        return response;
    }

    @Override
    public Boolean isValidCandidate(String paymentType) {
        return paymentModule.isValidCandidate(paymentType);
    }

}
