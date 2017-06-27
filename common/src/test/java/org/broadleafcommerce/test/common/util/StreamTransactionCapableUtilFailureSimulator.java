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
package org.broadleafcommerce.test.common.util;

import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

/**
 * Utility class that can be substituted for StreamingTransactionCapableUtil to allow targeted testing of
 * transaction failures (e.g. connection pool exhaustion)
 *
 * @author Jeff Fischer
 */
public class StreamTransactionCapableUtilFailureSimulator extends StreamingTransactionCapableUtil {

    public static final String FAILURE_MODE_KEY = "failureMode";
    public static final String FAILURE_MODE_PU = "failureModePU";
    public static final String FAILURE_MODE_EXCEPTION = "failureModeException";
    private static final String blPUCheckClassName = "org.broadleafcommerce.core.catalog.domain.ProductImpl";
    private static final String blEventPUCheckClassName = "com.broadleafcommerce.jobsevents.domain.SystemEventImpl";

    public void startFailureMode(RuntimeException exceptionToThrow, String persistenceUnit) {
        checkPU(persistenceUnit);
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        context.getAdditionalProperties().put(FAILURE_MODE_KEY, true);
        context.getAdditionalProperties().put(FAILURE_MODE_PU, persistenceUnit);
        context.getAdditionalProperties().put(FAILURE_MODE_EXCEPTION, exceptionToThrow);
    }

    public void endFailureMode(String persistenceUnit) {
        checkPU(persistenceUnit);
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        context.getAdditionalProperties().remove(FAILURE_MODE_KEY);
        context.getAdditionalProperties().remove(FAILURE_MODE_PU);
        context.getAdditionalProperties().remove(FAILURE_MODE_EXCEPTION);
    }

    @Override
    protected TransactionStatus startTransaction(int propagationBehavior, int isolationLevel, boolean readOnly, PlatformTransactionManager transactionManager) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context.getAdditionalProperties().containsKey(FAILURE_MODE_KEY)) {
            String failureModePU = (String) context.getAdditionalProperties().get(FAILURE_MODE_PU);
            String checkClassName = failureModePU.equals("blPU")?blPUCheckClassName:blEventPUCheckClassName;
            if (((HibernateEntityManagerFactory) ((JpaTransactionManager) transactionManager).getEntityManagerFactory())
                                    .getSessionFactory().getAllClassMetadata().containsKey(checkClassName)){
                throw (RuntimeException) context.getAdditionalProperties().get(FAILURE_MODE_EXCEPTION);
            }
        }
        return super.startTransaction(propagationBehavior, isolationLevel, readOnly, transactionManager);
    }

    protected void checkPU(String persistenceUnit) {
        if (!persistenceUnit.equals("blPU") && !persistenceUnit.equals("blEventPU")) {
            throw new UnsupportedOperationException(persistenceUnit + " not supported");
        }
    }
}
