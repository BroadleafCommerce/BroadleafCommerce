package org.broadleafcommerce.gwt.client.service;

/**
 * Exception thrown when a {@link EntityService service} method fails.
 */
public class ServiceException extends Exception {
    
    private static final long serialVersionUID = -7084792578727995587L;
    
    // for serialization purposes
    protected ServiceException() {
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ServiceException(String message) {
        super(message);
    }
    
}
