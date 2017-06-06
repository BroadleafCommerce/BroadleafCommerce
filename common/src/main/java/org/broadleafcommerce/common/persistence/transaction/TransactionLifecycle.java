package org.broadleafcommerce.common.persistence.transaction;

/**
 * Desribes the key transaction lifecycle events being monitored by the system
 *
 * @author Jeff Fischer
 */
public enum TransactionLifecycle {
    GET_TRANSACTION,BEGIN,COMMIT,ROLLBACK
}
