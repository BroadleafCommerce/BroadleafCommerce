/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
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
    protected TransactionStatus startTransaction(int propagationBehavior, int isolationLevel) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context.getAdditionalProperties().containsKey(FAILURE_MODE_KEY)) {
            String failureModePU = (String) context.getAdditionalProperties().get(FAILURE_MODE_PU);
            String checkClassName = failureModePU.equals("blPU")?blPUCheckClassName:blEventPUCheckClassName;
            if (((HibernateEntityManagerFactory) ((JpaTransactionManager) transactionManager).getEntityManagerFactory())
                                    .getSessionFactory().getAllClassMetadata().containsKey(checkClassName)){
                throw (RuntimeException) context.getAdditionalProperties().get(FAILURE_MODE_EXCEPTION);
            }
        }
        return super.startTransaction(propagationBehavior, isolationLevel);
    }

    protected void checkPU(String persistenceUnit) {
        if (!persistenceUnit.equals("blPU") && !persistenceUnit.equals("blEventPU")) {
            throw new UnsupportedOperationException(persistenceUnit + " not supported");
        }
    }
}
