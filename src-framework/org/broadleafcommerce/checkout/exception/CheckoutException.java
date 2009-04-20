package org.broadleafcommerce.checkout.exception;

public class CheckoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CheckoutException() {
        super();
    }

    public CheckoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckoutException(String message) {
        super(message);
    }

    public CheckoutException(Throwable cause) {
        super(cause);
    }

}
