package org.broadleafcommerce.openadmin.server.service.persistence;

/**
 * Specific class of PersistenceException used in FieldPersistenceProviders that attempt to perform their own persistence
 * operations in addition to the normal entity field population duties.
 *
 * @author Jeff Fischer
 */
public class ParentEntityPersistenceException extends PersistenceException {

    public ParentEntityPersistenceException(Throwable cause) {
        super(cause);
    }

    public ParentEntityPersistenceException(String message) {
        super(message);
    }

    public ParentEntityPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
