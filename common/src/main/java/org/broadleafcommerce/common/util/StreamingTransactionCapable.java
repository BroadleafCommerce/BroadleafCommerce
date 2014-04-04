/*
 * #%L
 * BroadleafCommerce Workflow
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

import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Jeff Fischer
 */
public interface StreamingTransactionCapable {

    int getPageSize();

    void setPageSize(int pageSize);

    int getRetryMax();

    void setRetryMax(int retryMax);

    <G extends Throwable> void runStreamingTransactionalOperation(final StreamCapableTransactionalOperation
                                                                          streamOperation, Class<G> exceptionType) throws G;

    <G extends Throwable> void runStreamingTransactionalOperation(final StreamCapableTransactionalOperation
                                                                          streamOperation, Class<G> exceptionType, int transactionBehavior, int isolationLevel) throws G;

    <G extends Throwable> void runTransactionalOperation(StreamCapableTransactionalOperation operation,
                                                         Class<G> exceptionType) throws G;

    <G extends Throwable> void runTransactionalOperation(StreamCapableTransactionalOperation operation,
                                                         Class<G> exceptionType, int transactionBehavior, int
            isolationLevel) throws G;

    <G extends Throwable> void runOptionalTransactionalOperation(StreamCapableTransactionalOperation operation,
                                                                 Class<G> exceptionType, boolean useTransaction)
            throws G;

    <G extends Throwable> void runOptionalTransactionalOperation(StreamCapableTransactionalOperation operation,
                                                                 Class<G> exceptionType, boolean useTransaction,
                                                                 int transactionBehavior, int isolationLevel) throws G;

    PlatformTransactionManager getTransactionManager();

    void setTransactionManager(PlatformTransactionManager transactionManager);

}
