package org.broadleafcommerce.pricing.exception;

public class PricingException extends Exception {

    private static final long serialVersionUID = 1L;

    public PricingException() {
        super();
    }

    public PricingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PricingException(String message) {
        super(message);
    }

    public PricingException(Throwable cause) {
        super(cause);
    }

}
