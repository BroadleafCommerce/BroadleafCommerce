/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.broadleafcommerce.common.exception.OptimisticLockInvalidStateException;
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

    /**
     * Performs an update operation on an entity within an optimistic lock aware transaction.
     *
     * @param <T> The type of entity you wish to update.
     */
    public interface UpdateOperation<T> {
        /**
         * Perform the update operations on the entity.
         *
         * @param t The entity as represented in the database during the current transaction.
         */
        void update(T t);
    }

    /**
     * Checks if the state of the entity is valid then performs an update operation on an entity within an optimistic
     * lock aware transaction.
     *
     * @param <T> The type of entity you wish to validate and update.
     */
    public interface ValidatedUpdateOperation<T> extends UpdateOperation<T> {
        /**
         * Check whether or not the {@link #update(Object)} operation is still valid given the current state of the
         * entity.
         *
         * @param t The entity as represented in the database during the current transaction.
         * @return true if the entity is in a valid state to perform the {@link #update(Object)} operation, false
         * otherwise.
         */
        boolean isValid(T t);
    }

    /**
     * Perform an update on a entity that supports optimistic locking.
     * <p>
     * This method will read the entity from the database, perform the update operation provided, and attempt to commit
     * the transaction. If the transaction cannot be committed due to an {@link OptimisticLockException} then the
     * operation will be retried until {@code maxRetryCount} is reached.
     * <p>
     * If a {@link ValidatedUpdateOperation} is passed as the {@code operation} parameter, then the {@link
     * ValidatedUpdateOperation#isValid(Object)} method will be called after the read but before the update. If this
     * call returns false, then the update will abort with a {@link OptimisticLockInvalidStateException}.
     * <p>
     * Optimistic locking can be enabled on an entity by adding a variable with the {@link Version} annotation.
     *
     * @throws OptimisticLockMaxRetryException     if an {@link OptimisticLockException} occurs {@code maxRetryCount}
     *                                             times.
     * @throws OptimisticLockInvalidStateException if the entity state is found to be invalid due to {@link
     *                                             ValidatedUpdateOperation#isValid(Object)} returning false.
     */
    public static <T> T performOptimisticLockUpdate(String name, UpdateOperation<T> operation, Class<? extends T> entityClass, Object identifier, int maxRetryCount, PlatformTransactionManager transactionManager, EntityManager entityManager) throws OptimisticLockMaxRetryException, OptimisticLockInvalidStateException {
        int retryCount = 0;
        boolean saveSuccessful = false;
        T entity = null;
        while (!saveSuccessful) {
            if (retryCount >= maxRetryCount) {
                log.debug("Max retry count was reached while trying to perform " + name + " on " + entityClass.getSimpleName()+ " with id: " + identifier);
                throw new OptimisticLockMaxRetryException("Unable to perform " + name + " on " + entityClass.getSimpleName() + " with id: " + identifier + ". " +
                        "Tried " + retryCount + " times, but the version for this entity continues to be concurrently modified.");
            }
            try {
                entity = doTransactionalOptimisticUpdate(name, operation, entityClass, identifier, transactionManager, entityManager);
                saveSuccessful = true;
                log.debug(name + " for " + entityClass.getSimpleName() + " with ID: " + identifier + " performed " + retryCount + " retries.");

            } catch (OptimisticLockException e) {
                log.debug("Optimistic locking failure. Concurrent modification detected when attempting to modify " + entityClass.getSimpleName() + " with id: " + identifier);
            }
            retryCount++;
        }
        return entity;
    }

    protected static <T> T doTransactionalOptimisticUpdate(String name, UpdateOperation<T> operation, Class<? extends T> entityClass, Object identifier, PlatformTransactionManager transactionManager, EntityManager entityManager) {
        TransactionStatus transactionStatus = TransactionUtils.createTransaction(
                name,
                TransactionDefinition.PROPAGATION_REQUIRES_NEW,
                transactionManager);
        T entity;
        try {
            entity = entityManager.find(entityClass, identifier);
            if (operation instanceof ValidatedUpdateOperation && !((ValidatedUpdateOperation<T>) operation).isValid(entity)) {
                log.debug("Entity state was found to be invalid while trying to perform " + name + " on " + entityClass.getSimpleName() + " with id: " + identifier);
                throw new OptimisticLockInvalidStateException("Unable to perform " + name + " on " + entityClass.getSimpleName() + " with id: " + identifier + ". Aborting update due to invalid state.");
            }
            operation.update(entity);
            entityManager.flush();

            TransactionUtils.finalizeTransaction(transactionStatus, transactionManager, false);

        } catch (RuntimeException e) {
            TransactionUtils.finalizeTransaction(transactionStatus, transactionManager, true);
            throw e;
        }
        return entity;
    }
}
