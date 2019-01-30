/*
 * #%L
 * BroadleafCommerce Workflow
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

import org.springframework.transaction.PlatformTransactionManager;

/**
 * Utility used for running transactional operations. Streaming operations are interesting because you may want to iterate
 * over a large set of data and perform some work in a transaction. In order to limit transaction times and conserve heap,
 * iterating over the large set should be done in chunks and the level 1 cache should be clear between each chunk. This
 * utility abstracts the creation of chunks and small transactions and level 1 cache clearing so that the caller only need
 * worry about coding the work to be done.
 *
 * @author Jeff Fischer
 */
public interface StreamingTransactionCapable {

    /**
     * The result set size per page of data when streaming. See {@link #runStreamingTransactionalOperation(StreamCapableTransactionalOperation, Class)}.
     * @return
     */
    int getPageSize();

    void setPageSize(int pageSize);

    int getRetryMax();

    void setRetryMax(int retryMax);

    /**
     * Run a streaming operation inside a transaction. Uses the {@link StreamCapableTransactionalOperation} API to determine
     * the page of data to work on within the transaction scope.
     *
     * @param streamOperation
     * @param exceptionType
     * @param <G>
     * @throws G
     */
    <G extends Throwable> void runStreamingTransactionalOperation(StreamCapableTransactionalOperation
                                                                          streamOperation, Class<G> exceptionType) throws G;

    <G extends Throwable> void runTransactionalOperation(StreamCapableTransactionalOperation operation,
                Class<G> exceptionType, PlatformTransactionManager transactionManager) throws G;

    <G extends Throwable> void runStreamingTransactionalOperation(StreamCapableTransactionalOperation streamOperation,
                                                                  Class<G> exceptionType, int transactionBehavior,
                                                                  int isolationLevel) throws G;

    /**
     * Run an operation inside of a single transaction. This is not a streaming use case and represents a basic operation
     * to perform in a transaction. See {@link StreamCapableTransactionalOperation#execute()}.
     *
     * @param operation
     * @param exceptionType
     * @param <G>
     * @throws G
     */
    <G extends Throwable> void runTransactionalOperation(StreamCapableTransactionalOperation operation,
                                                                    Class<G> exceptionType) throws G;

    <G extends Throwable> void runTransactionalOperation(StreamCapableTransactionalOperation operation,
                                                                    Class<G> exceptionType, int transactionBehavior,
                                                                    int isolationLevel) throws G;

    /**
     * Run an operation inside of a single transaction. This is not a streaming use case and represents a basic operation
     * to perform in a transaction. See {@link StreamCapableTransactionalOperation#execute()}. The useTransaction parameter
     * allows the inclusion of some logic to determine whether or not the creation of the transaction is required for the operation. This is
     * useful in situations where there may already be an active transaction that you want to use, if available.
     *
     * @param operation
     * @param exceptionType
     * @param useTransaction
     * @param <G>
     * @throws G
     */
    <G extends Throwable> void runOptionalTransactionalOperation(StreamCapableTransactionalOperation operation,
                                                                 Class<G> exceptionType, boolean useTransaction) throws G;

    <G extends Throwable> void runOptionalTransactionalOperation(StreamCapableTransactionalOperation operation,
                                                                 Class<G> exceptionType, boolean useTransaction,
                                                                 int transactionBehavior, int isolationLevel) throws G;

    <G extends Throwable> void runOptionalTransactionalOperation(StreamCapableTransactionalOperation operation,
                                                                Class<G> exceptionType, boolean useTransaction,
                                                                int transactionBehavior, int isolationLevel,
                                                                boolean readOnly, PlatformTransactionManager transactionManager) throws G;

    PlatformTransactionManager getTransactionManager();

    void setTransactionManager(PlatformTransactionManager transactionManager);

    /**
     * Executes the Runnable operation in the scope of an Entity-Manager-In-View pattern, if there is not already an
     * EntityManager on the thread. This is useful for operations that may be susceptible to lazy init exceptions if there
     * is not an EntityManager available on the thread.
     *
     * @param runnable
     */
    void runOptionalEntityManagerInViewOperation(Runnable runnable);

}
