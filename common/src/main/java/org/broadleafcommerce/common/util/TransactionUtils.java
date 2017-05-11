/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
     * Intended for use in all @Transactional definitions that operate against the <pre>blSecurePU</pre> persistence unit. For instance:
     * <pre>@Transactional(TransactionUtils.SECURE_TRANSACTION_MANAGER)</pre>
     */
    public static final String SECURE_TRANSACTION_MANAGER = "blTransactionManagerSecureInfo";
    
    private static final Log LOG = LogFactory.getLog(TransactionUtils.class);

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
