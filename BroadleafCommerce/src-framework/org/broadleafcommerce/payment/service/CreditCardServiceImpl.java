package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.exception.PaymentProcessorException;
import org.broadleafcommerce.payment.service.module.CreditCardModule;
import org.broadleafcommerce.payment.service.module.PaymentResponse;

public class CreditCardServiceImpl implements CreditCardService {

    protected CreditCardModule creditCardModule;

    public CreditCardModule getCreditCardModule() {
        return creditCardModule;
    }

    public void setCreditCardModule(CreditCardModule creditCardModule) {
        this.creditCardModule = creditCardModule;
    }

    public PaymentResponse authorize(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException, PaymentProcessorException {
        return creditCardModule.authorize(paymentInfo, creditCardPaymentInfo);
    }

    public PaymentResponse authorizeAndDebit(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException, PaymentProcessorException {
        return creditCardModule.authorizeAndDebit(paymentInfo, creditCardPaymentInfo);
    }

    public PaymentResponse credit(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException, PaymentProcessorException {
        return creditCardModule.credit(paymentInfo, creditCardPaymentInfo);
    }

    public PaymentResponse debit(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException, PaymentProcessorException {
        return creditCardModule.debit(paymentInfo, creditCardPaymentInfo);
    }

    public PaymentResponse voidPayment(PaymentInfo paymentInfo, CreditCardPaymentInfo creditCardPaymentInfo) throws PaymentException, PaymentProcessorException {
        return creditCardModule.voidPayment(paymentInfo, creditCardPaymentInfo);
    }

}
