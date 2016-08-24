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
