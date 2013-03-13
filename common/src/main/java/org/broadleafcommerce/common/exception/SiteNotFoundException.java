package org.broadleafcommerce.common.exception;

/**
 * @author Jeff Fischer
 */
public class SiteNotFoundException extends Exception {

    public SiteNotFoundException() {
        //do nothing
    }

    public SiteNotFoundException(Throwable cause) {
        super(cause);
    }

    public SiteNotFoundException(String message) {
        super(message);
    }

    public SiteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
