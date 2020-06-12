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
package org.broadleafcommerce.common.cache;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

/**
 * Specific implementation used with a Log4j dependency
 * @author Elbert Bautista (elbertbautista)
 * @deprecated in favor of {@link Log4j2StatisticsServiceLogAdapter} (following Apache's EOL declaration for log4j 1.x)
 */
@Deprecated
public class Log4jStatisticsServiceLogAdapter implements StatisticsServiceLogAdapter {

    @Override
    public void activateLogging(Class clazz) {
        LogManager.getLogger(clazz).setLevel(Level.INFO);
    }

    @Override
    public void disableLogging(Class clazz) {
        LogManager.getLogger(StatisticsServiceImpl.class).setLevel(Level.DEBUG);
    }

}
