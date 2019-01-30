/*
 * #%L
 * BroadleafCommerce Common Libraries
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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;

/**
 * @author Jeff Fischer
 */
public class TransactionUtils {

    /**
     * Intended for use in all @Transactional definitions that operate against the <pre>blPU</pre> persistence unit. For instance:
     * <pre>@Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)</pre>
     */
    public static final String DEFAULT_TRANSACTION_MANAGER = "blTransactionManager";

    /**
     * Intended for use in all @Transactional definitions that operate against the <pre>blEventPU</pre> persistence unit. For instance:
     * <pre>@Transactional(TransactionUtils.EVENT_TRANSACTION_MANAGER)</pre>
     */
    public static final String EVENT_TRANSACTION_MANAGER = "blTransactionManagerEventInfo";

    /**
     * Intended for use in all @Transactional definitions that operate against the <pre>blSecurePU</pre> persistence unit. For instance:
     * <pre>@Transactional(TransactionUtils.SECURE_TRANSACTION_MANAGER)</pre>
     */
    public static final String SECURE_TRANSACTION_MANAGER = "blTransactionManagerSecureInfo";
    
    private static final Log LOG = LogFactory.getLog(TransactionUtils.class);

    public static Transaction createTransaction(Session session) {
        return session.beginTransaction();
    }

    public static TransactionStatus createTransaction(String name, int propagationBehavior, PlatformTransactionManager transactionManager) {
        return createTransaction(name, propagationBehavior, transactionManager, false);
    }

    public static TransactionStatus createTransaction(String name, int propagationBehavior, PlatformTransactionManager transactionManager, boolean isReadOnly) {
        return createTransaction(name, propagationBehavior, TransactionDefinition.ISOLATION_DEFAULT, transactionManager, isReadOnly);
    }

    public static TransactionStatus createTransaction(String name, int propagationBehavior, int isolationLevel, PlatformTransactionManager transactionManager, boolean isReadOnly) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName(name);
        def.setReadOnly(isReadOnly);
        def.setPropagationBehavior(propagationBehavior);
        def.setIsolationLevel(isolationLevel);
        return transactionManager.getTransaction(def);
    }

    public static TransactionStatus createTransaction(int propagationBehavior, PlatformTransactionManager transactionManager, boolean isReadOnly) {
        return createTransaction(propagationBehavior, TransactionDefinition.ISOLATION_DEFAULT, transactionManager, isReadOnly);
    }

    public static TransactionStatus createTransaction(int propagationBehavior, int isolationLevel, PlatformTransactionManager transactionManager, boolean isReadOnly) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setReadOnly(isReadOnly);
        def.setPropagationBehavior(propagationBehavior);
        def.setIsolationLevel(isolationLevel);
        return transactionManager.getTransaction(def);
    }

    public static boolean isTransactionalEntityManager(EntityManager em) {
        EntityManager target = EntityManagerFactoryUtils.doGetTransactionalEntityManager(
        					em.getEntityManagerFactory(), em.getProperties(), true);
        return target != null;
    }

    public static void finalizeTransaction(Transaction transaction, boolean isError) {
        if (isError) {
            try {
                transaction.rollback();
            } catch (Exception e) {
                LOG.error("Rolling back caused exception. Logging and continuing.", e);
            }
        } else {
            transaction.commit();
        }
    }

    public static void finalizeTransaction(TransactionStatus status, PlatformTransactionManager transactionManager, boolean isError) {
        boolean isActive = false;
        try {
            if (!status.isRollbackOnly()) {
                isActive = true;
            }
        } catch (Exception e) {
            //do nothing
        }
        if (isError || !isActive) {
            try {
                transactionManager.rollback(status);
            } catch (Exception e) {
                LOG.error("Rolling back caused exception. Logging and continuing.", e);
            }
        } else {
            transactionManager.commit(status);
        }
    }

}
