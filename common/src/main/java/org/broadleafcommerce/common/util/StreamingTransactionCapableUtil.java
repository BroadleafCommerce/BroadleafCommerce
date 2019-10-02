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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author Jeff Fischer
 */
@Component("blStreamingTransactionCapableUtil")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StreamingTransactionCapableUtil implements StreamingTransactionCapable {

    private static final Log LOG = LogFactory.getLog(StreamingTransactionCapableUtil.class);

    @Resource(name = "blTransactionManager")
    protected PlatformTransactionManager platformTransactionManager;

    protected EntityManager em;

    @Value("${streaming.transaction.lock.retry.max}")
    protected int retryMax = 10;

    @Value("${streaming.transaction.item.page.size}")
    protected int pageSize;

    @PostConstruct
    public void init() {
        if (getTransactionManager() instanceof JpaTransactionManager) {
            em = ((JpaTransactionManager) getTransactionManager()).getEntityManagerFactory().createEntityManager();
        }
    }

    @Override
    public <G extends Throwable> void runStreamingTransactionalOperation(final StreamCapableTransactionalOperation
                                        streamOperation, Class<G> exceptionType) throws G {
        runStreamingTransactionalOperation(streamOperation, exceptionType, TransactionDefinition.PROPAGATION_REQUIRED, TransactionDefinition.ISOLATION_DEFAULT);
    }

    @Override
    public <G extends Throwable> void runStreamingTransactionalOperation(final StreamCapableTransactionalOperation
                                        streamOperation, Class<G> exceptionType, int transactionBehavior, int isolationLevel) throws G {
        //this should be a read operation, so doesn't need to be in a transaction
        final Long totalCount = streamOperation.retrieveTotalCount();
        final Holder holder = new Holder();
        holder.setVal(0);
        StreamCapableTransactionalOperation operation = new StreamCapableTransactionalOperationAdapter() {
            @Override
            public void execute() throws Throwable {
                pagedItems = streamOperation.retrievePage(holder.getVal(), pageSize);
                streamOperation.pagedExecute(pagedItems);

                int pagedItemCount = ((Collection) pagedItems[0]).size();
                if (pagedItemCount == 0) {
                    holder.setVal(totalCount.intValue());
                } else {
                    if (LOG.isDebugEnabled() && !isFinalPage(holder, pagedItemCount, totalCount) && (pagedItemCount != pageSize)) {
                        LOG.debug(String.format("In the previous iteration of this streaming transactional operation, " +
                                "(%s) pagedItems were processed when we were expecting a full page of (%s) items. " +
                                "Please ensure that your StreamCapableTransactionalOperation#retrieveTotalCount() " +
                                "and StreamCapableTransactionalOperation#retrievePage(int startPos, int pageSize) " +
                                "queries contain the same conditions as to ultimately provide the number of entities " +
                                "equal to the declared total count. Stream operation: %s",
                                pagedItemCount, pageSize, streamOperation.getClass()));
                    }

                    if (pagedItemCount < pageSize) {
                        holder.setVal(holder.getVal() + pageSize);
                    } else {
                        holder.setVal(holder.getVal() + pagedItemCount);
                    }
                }
            }

            private boolean isFinalPage(Holder holder, int pagedItemCount, Long totalCount) {
                int processedItemCount = holder.getVal() + pagedItemCount;

                return processedItemCount >= totalCount;
            }
        };
        while (holder.getVal() < totalCount) {
            runOptionalTransactionalOperation(operation, exceptionType, true, transactionBehavior, isolationLevel, false, getTransactionManager());
            if (em != null) {
                //The idea behind using this class is that it will likely process a lot of records. As such, it is necessary
                //to clear the level 1 cache after each iteration so that we don't run out of heap
                em.clear();
            }
            streamOperation.executeAfterCommit(((StreamCapableTransactionalOperationAdapter) operation).getPagedItems());
        }
    }

    @Override
    public <G extends Throwable> void runTransactionalOperation(StreamCapableTransactionalOperation operation,
                                        Class<G> exceptionType) throws G {
        runOptionalTransactionalOperation(operation, exceptionType, true, TransactionDefinition.PROPAGATION_REQUIRED, TransactionDefinition.ISOLATION_DEFAULT, false, getTransactionManager());
    }

    @Override
    public <G extends Throwable> void runTransactionalOperation(StreamCapableTransactionalOperation operation,
            Class<G> exceptionType, PlatformTransactionManager transactionManager) throws G {
        runOptionalTransactionalOperation(operation, exceptionType, true, TransactionDefinition.PROPAGATION_REQUIRED, TransactionDefinition.ISOLATION_DEFAULT, false, transactionManager);
    }

    @Override
    public <G extends Throwable> void runTransactionalOperation(StreamCapableTransactionalOperation operation,
                                        Class<G> exceptionType, int transactionBehavior, int isolationLevel) throws G {
        runOptionalTransactionalOperation(operation, exceptionType, true, transactionBehavior, isolationLevel, false, getTransactionManager());
    }

    @Override
    public <G extends Throwable> void runOptionalTransactionalOperation(StreamCapableTransactionalOperation operation,
                                        Class<G> exceptionType, boolean useTransaction) throws G {
        runOptionalTransactionalOperation(operation, exceptionType, useTransaction, TransactionDefinition.PROPAGATION_REQUIRED, TransactionDefinition.ISOLATION_DEFAULT, false, getTransactionManager());
    }

    @Override
    public <G extends Throwable> void runOptionalTransactionalOperation(StreamCapableTransactionalOperation operation,
                                            Class<G> exceptionType, boolean useTransaction, int transactionBehavior, int isolationLevel) throws G {
        runOptionalTransactionalOperation(operation, exceptionType, useTransaction, transactionBehavior, isolationLevel, false, getTransactionManager());
    }

    @Override
    public void runOptionalEntityManagerInViewOperation(Runnable runnable) {
        EntityManagerFactory emf = ((JpaTransactionManager) getTransactionManager()).getEntityManagerFactory();
        boolean isEntityManagerInView = TransactionSynchronizationManager.hasResource(emf);
        try {
            if (!isEntityManagerInView) {
                EntityManager em = emf.createEntityManager();
                EntityManagerHolder emHolder = new EntityManagerHolder(em);
                TransactionSynchronizationManager.bindResource(emf, emHolder);
            }
            runnable.run();
        } finally {
            if (!isEntityManagerInView) {
                EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager.unbindResource(emf);
                EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
            }
        }
    }

    @Override
    public <G extends Throwable> void runOptionalTransactionalOperation(StreamCapableTransactionalOperation operation,
                                        Class<G> exceptionType, boolean useTransaction, int transactionBehavior, int isolationLevel,
                                        boolean readOnly, PlatformTransactionManager transactionManager) throws G {
        int maxCount = operation.retryMaxCountOverrideForLockAcquisitionFailure();
        if (maxCount == -1) {
            maxCount = retryMax;
        }
        int tryCount = 0;
        boolean retry = false;
        do {
            tryCount++;
            try {
                TransactionStatus status = null;
                if (useTransaction) {
                    status = startTransaction(transactionBehavior, isolationLevel, readOnly, transactionManager);
                }
                boolean isError = false;
                try {
                    operation.execute();
                    retry = false;
                } catch (Throwable e) {
                    isError = true;
                    ExceptionHelper.processException(exceptionType, RuntimeException.class, e);
                } finally {
                    if (useTransaction) {
                        endTransaction(status, isError, exceptionType, transactionManager);
                    }
                }
            } catch (RuntimeException e) {
                checkException: {
                    if (operation.shouldRetryOnTransactionLockAcquisitionFailure()) {
                        Exception result = ExceptionHelper.refineException(LockAcquisitionException.class, RuntimeException.class, e);
                        if (result.getClass().equals(LockAcquisitionException.class)) {
                            if (tryCount < maxCount) {
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException ie) {
                                    //do nothing
                                }
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Unable to acquire a transaction lock. Retrying - count(" + tryCount + ").");
                                }
                                retry = true;
                                break checkException;
                            }
                            LOG.warn("Unable to acquire a transaction lock after " + maxCount + " tries.");
                        }
                    }
                    throw e;
                }
            }
        } while (tryCount < maxCount && retry && operation.shouldRetryOnTransactionLockAcquisitionFailure());
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return platformTransactionManager;
    }

    @Override
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.platformTransactionManager = transactionManager;
        init();
    }

    @Override
    public int getRetryMax() {
        return retryMax;
    }

    @Override
    public void setRetryMax(int retryMax) {
        this.retryMax = retryMax;
    }

    protected <G extends Throwable> void endTransaction(TransactionStatus status, boolean error, Class<G> exceptionType, PlatformTransactionManager transactionManager) throws G {
        try {
            TransactionUtils.finalizeTransaction(status, transactionManager, error);
        } catch (Throwable e) {
            ExceptionHelper.processException(exceptionType, RuntimeException.class, e);
        }
    }

    protected TransactionStatus startTransaction(int propagationBehavior, int isolationLevel, boolean isReadOnly, PlatformTransactionManager transactionManager) {
        TransactionStatus status;
        try {
            status = TransactionUtils.createTransaction(propagationBehavior, isolationLevel,
                    transactionManager, isReadOnly);
        } catch (RuntimeException e) {
            LOG.error("Could not start transaction", e);
            throw e;
        }
        return status;
    }

    private class Holder {

        private int val;

        public int getVal() {
            return val;
        }

        public void setVal(int val) {
            this.val = val;
        }
    }
}
