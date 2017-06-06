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
