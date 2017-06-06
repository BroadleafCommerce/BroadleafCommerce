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
