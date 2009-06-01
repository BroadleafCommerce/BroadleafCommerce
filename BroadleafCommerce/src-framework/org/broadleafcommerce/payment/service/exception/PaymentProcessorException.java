package org.broadleafcommerce.payment.service.exception;

import org.broadleafcommerce.order.domain.PaymentResponseItem;

public class PaymentProcessorException extends PaymentException {

    private static final long serialVersionUID = 1L;

    protected PaymentResponseItem paymentResponseItem;

    public PaymentProcessorException(PaymentResponseItem paymentResponseItem) {
        super();
        this.paymentResponseItem = paymentResponseItem;
    }

    public PaymentProcessorException(String message, Throwable cause, PaymentResponseItem paymentResponseItem) {
        super(message, cause);
        this.paymentResponseItem = paymentResponseItem;
    }

    public PaymentProcessorException(String message, PaymentResponseItem paymentResponseItem) {
        super(message);
        this.paymentResponseItem = paymentResponseItem;
    }

    public PaymentProcessorException(Throwable cause, PaymentResponseItem paymentResponseItem) {
        super(cause);
        this.paymentResponseItem = paymentResponseItem;
    }

}
