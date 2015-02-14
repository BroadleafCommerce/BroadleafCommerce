package org.broadleafcommerce.common.exception;

/**
 * Special exception thrown when a problem is encountered while trying to retrieve the original implementation
 * class for a proxy. This is generally in relation to getting the original entity implementation from
 * a Hibernate javassist proxy.
 *
 * @author Jeff Fischer
 */
public class ProxyDetectionException extends RuntimeException {

    public ProxyDetectionException() {
    }

    public ProxyDetectionException(String message) {
        super(message);
    }

    public ProxyDetectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyDetectionException(Throwable cause) {
        super(cause);
    }

    public ProxyDetectionException(String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
