/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
