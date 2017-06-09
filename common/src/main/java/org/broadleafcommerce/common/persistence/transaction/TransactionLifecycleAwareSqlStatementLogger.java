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

import org.hibernate.engine.jdbc.internal.Formatter;
import org.hibernate.engine.jdbc.spi.SqlStatementLogger;

/**
 * Custom {@link SqlStatementLogger} that will log sql statements during a transaction with {@link TransactionLifecycleMonitor}.
 *
 * @author Jeff Fischer
 */
public class TransactionLifecycleAwareSqlStatementLogger extends SqlStatementLogger {

    public TransactionLifecycleAwareSqlStatementLogger() {
    }

    public TransactionLifecycleAwareSqlStatementLogger(boolean logToStdout, boolean format) {
        super(logToStdout, format);
    }

    @Override
    public void logStatement(String statement, Formatter formatter) {
        super.logStatement(statement, formatter);
        SqlStatementLoggable monitor = TransactionLifecycleMonitor.getInstance();
        if (monitor != null) {
            monitor.log(statement);
        }
    }
}
