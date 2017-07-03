/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.persistence.transaction;

import org.hibernate.engine.jdbc.internal.JdbcServicesImpl;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;

/**
 * A customization of {@link JdbcServicesImpl} that allows further usage of the sql statement logging. This is useful
 * for tracking all sql executed during the lifecycle of a transaction.
 *
 * @see TransactionLifecycleAwareSqlStatementLogger
 * @see TransactionLifecycleMonitor
 * @author Jeff Fischer
 */
public class LifecycleAwareJDBCServices extends JdbcServicesImpl {

    @Override
    public SqlStatementLogger getSqlStatementLogger() {
        SqlStatementLogger defaultLogger = super.getSqlStatementLogger();
        return new TransactionLifecycleAwareSqlStatementLogger(defaultLogger.isLogToStdout(), defaultLogger.isFormat());
    }
}
