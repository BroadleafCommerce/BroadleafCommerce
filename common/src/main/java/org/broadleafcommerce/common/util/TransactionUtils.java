package org.broadleafcommerce.common.util;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author Jeff Fischer
 */
public class TransactionUtils {

    public static TransactionStatus createTransaction(String name, int propagationBehavior, PlatformTransactionManager transactionManager) {
        return createTransaction(name, propagationBehavior, transactionManager, false);
    }

    public static TransactionStatus createTransaction(String name, int propagationBehavior, PlatformTransactionManager transactionManager, boolean isReadOnly) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName(name);
        def.setReadOnly(isReadOnly);
        def.setPropagationBehavior(propagationBehavior);
        return transactionManager.getTransaction(def);
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
        if (isActive) {
            if (isError) {
                transactionManager.rollback(status);
            } else {
                transactionManager.commit(status);
            }
        }
    }

}
