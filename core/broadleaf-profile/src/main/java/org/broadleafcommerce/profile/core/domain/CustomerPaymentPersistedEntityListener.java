package org.broadleafcommerce.profile.core.domain;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import org.broadleafcommerce.common.util.ApplicationContextHolder;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class CustomerPaymentPersistedEntityListener {
    /**
     * Invoked on PostPersist, PostUpdate, and PostRemove. The default implementation is to simply publish a Spring event
     * to the ApplicationContext after the transaction has completed.
     * 
     * @param entity the newly-persisted CustomerPayment
     * @see CustomerPersistedEvent
     */
    @PostPersist
    @PostUpdate
    @PostRemove
    public void customerPaymentUpdated(final Object entity) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    ApplicationContextHolder.getApplicationContext().publishEvent(new CustomerPersistedEvent(((CustomerPayment) entity).getCustomer()));
                }
            });
        }
    }
}
