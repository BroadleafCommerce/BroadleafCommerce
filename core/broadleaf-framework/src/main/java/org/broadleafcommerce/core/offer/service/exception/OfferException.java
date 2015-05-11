package org.broadleafcommerce.core.offer.service.exception;

import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;


public class OfferException extends CheckoutException {

    private static final long serialVersionUID = 1L;

    public OfferException() {
        super();
    }
    
    public OfferException(String message) {
        super(message, null);
    }

    public OfferException(String message, Throwable cause, CheckoutSeed seed) {
        super(message, cause, seed);
    }

    public OfferException(String message, CheckoutSeed seed) {
        super(message, seed);
    }

    public OfferException(Throwable cause, CheckoutSeed seed) {
        super(cause, seed);
    }
}
