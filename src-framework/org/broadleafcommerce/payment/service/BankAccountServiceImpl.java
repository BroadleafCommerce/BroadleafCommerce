package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.BankAccountPaymentInfo;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.exception.PaymentProcessorException;
import org.broadleafcommerce.payment.service.module.BankAccountModule;
import org.broadleafcommerce.payment.service.module.PaymentResponse;

public class BankAccountServiceImpl implements BankAccountService {

    protected BankAccountModule bankAccountModule;

    public PaymentResponse authorize(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException, PaymentProcessorException {
        return bankAccountModule.authorize(paymentInfo, bankAccountPaymentInfo);
    }

    public PaymentResponse authorizeAndDebit(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException, PaymentProcessorException {
        return bankAccountModule.authorizeAndDebit(paymentInfo, bankAccountPaymentInfo);
    }

    public PaymentResponse credit(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException, PaymentProcessorException {
        return bankAccountModule.credit(paymentInfo, bankAccountPaymentInfo);
    }

    public PaymentResponse debit(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException, PaymentProcessorException {
        return bankAccountModule.debit(paymentInfo, bankAccountPaymentInfo);
    }

    public PaymentResponse voidPayment(PaymentInfo paymentInfo, BankAccountPaymentInfo bankAccountPaymentInfo) throws PaymentException, PaymentProcessorException {
        return bankAccountModule.voidPayment(paymentInfo, bankAccountPaymentInfo);
    }

    public BankAccountModule getBankAccountModule() {
        return bankAccountModule;
    }

    public void setBankAccountModule(BankAccountModule bankAccountModule) {
        this.bankAccountModule = bankAccountModule;
    }

}
