/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.util;

import org.broadleafcommerce.common.exception.OptimisticLockMaxRetryException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.Version;

import lombok.extern.apachecommons.CommonsLog;

/**
 * Utility class for operations on entities that support optimistic locking.
 *
 * @author Philip Baggett (pbaggett)
 */
@CommonsLog
public class OptimisticLockUtils {

    public interface UpdateOperation<T> {
        void update(T t);
    }

    /**
     * Perform an update on a entity that supports optimistic locking.
     * <p>
     * This method will read the entity from the database, perform the update operation provided, and attempt to commit
     * the transaction. If the transaction cannot be committed due to an {@link OptimisticLockException} then the
     * operation will be retried until {@code maxRetryCount} is reached.
     * <p>
     * Optimistic locking can be enabled on an entity by adding a variable with the {@link Version} annotation.
     *
     * @throws OptimisticLockMaxRetryException if an {@link OptimisticLockException} occurs {@code maxRetryCount} times.
     */
    public static <T> T performOptimisticLockUpdate(String name, UpdateOperation operation, T t, Object identifier, int maxRetryCount, PlatformTransactionManager transactionManager, EntityManager entityManager) throws OptimisticLockMaxRetryException {
        int retryCount = 0;
        boolean saveSuccessful = false;
        while (!saveSuccessful) {
            if (retryCount >= maxRetryCount) {
                throw new OptimisticLockMaxRetryException("Unable to perform " + name + " on " + t.getClass().getSimpleName() + " with id: " + identifier + ". " +
                        "Tried " + retryCount + " times, but the version for this entity continues to be concurrently modified.");
            }
            try {
                t = doTransactionalOptimisticUpdate(name, operation, t, identifier, transactionManager, entityManager);
                saveSuccessful = true;
                log.debug(name + " for " + t.getClass().getSimpleName() + " with ID: " + identifier + " performed " + retryCount + " retries.");

            } catch (OptimisticLockException e) {
                log.debug("Optimistic locking failure. Concurrent modification detected when attempting to modify " + t.getClass().getSimpleName() + " with id: " + identifier);
            }
            retryCount++;
        }
        return t;
    }

    protected static <T> T doTransactionalOptimisticUpdate(String name, UpdateOperation operation, T t, Object identifier, PlatformTransactionManager transactionManager, EntityManager entityManager) {
        TransactionStatus transactionStatus = TransactionUtils.createTransaction(
                name,
                TransactionDefinition.PROPAGATION_REQUIRES_NEW,
                transactionManager);
        try {
            t = (T) entityManager.find(t.getClass(), identifier);
            operation.update(t);
            entityManager.flush();

            TransactionUtils.finalizeTransaction(transactionStatus, transactionManager, false);

        } catch (RuntimeException e) {
            TransactionUtils.finalizeTransaction(transactionStatus, transactionManager, true);
            throw e;
        }
        return t;
    }
}
