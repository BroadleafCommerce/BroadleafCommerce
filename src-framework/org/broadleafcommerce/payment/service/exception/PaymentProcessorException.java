package org.broadleafcommerce.payment.service.exception;

import org.broadleafcommerce.payment.service.module.PaymentResponse;

public class PaymentProcessorException extends Exception {

    private static final long serialVersionUID = 1L;

    protected PaymentResponse paymentResponse;

    public PaymentProcessorException(PaymentResponse paymentResponse) {
        super();
        this.paymentResponse = paymentResponse;
    }

    public PaymentProcessorException(String message, Throwable cause, PaymentResponse paymentResponse) {
        super(message, cause);
        this.paymentResponse = paymentResponse;
    }

    public PaymentProcessorException(String message, PaymentResponse paymentResponse) {
        super(message);
        this.paymentResponse = paymentResponse;
    }

    public PaymentProcessorException(Throwable cause, PaymentResponse paymentResponse) {
        super(cause);
        this.paymentResponse = paymentResponse;
    }

}
