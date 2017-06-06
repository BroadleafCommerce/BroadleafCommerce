package org.broadleafcommerce.common.persistence.transaction;

/**
 * Capable of logging sql statements
 *
 * @author Jeff Fischer
 */
public interface SqlStatementLoggable {

    void log(String statement);

}
