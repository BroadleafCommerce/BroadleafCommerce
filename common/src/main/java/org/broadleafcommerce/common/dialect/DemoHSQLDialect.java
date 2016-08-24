package org.broadleafcommerce.common.dialect;

import org.hibernate.dialect.HSQLDialect;

/**
 * A HSQL DB dialect specifically for the demo. In the demo use case, the database is empty on startup and does not
 * require table drops. Removing the table drop phase stops a number of HHH000389 level Hibernate errors from being
 * emitted to the console. While these exceptions are harmless, their occurrence should be avoided.
 *
 * @author Jeff Fischer
 */
public class DemoHSQLDialect extends HSQLDialect {

    @Override
    public boolean dropConstraints() {
        return false;
    }

}
