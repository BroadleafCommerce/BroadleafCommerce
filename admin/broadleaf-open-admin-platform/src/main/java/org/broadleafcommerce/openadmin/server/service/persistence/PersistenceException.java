package org.broadleafcommerce.openadmin.server.service.persistence;

/**
 * @author Jeff Fischer
 */
public class PersistenceException extends RuntimeException {

    public PersistenceException() {
        super();
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
