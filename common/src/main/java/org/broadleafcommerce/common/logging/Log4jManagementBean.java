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
package org.broadleafcommerce.common.logging;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * This is not hooked up by default so that Log4j is not required. If you are using Log4j, you can add this class to your
 * Spring applicationContext to enable it.
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @deprecated in favor of {@link Log4j2ManagementBean} (following Apache's EOL declaration for log4j 1.x)
 */
@Deprecated
@ManagedResource(objectName="org.broadleafcommerce:name=Log4JManangement", description="Logging Management", currencyTimeLimit=15)
public class Log4jManagementBean {

    @ManagedOperation(description="Activate info level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j category to set")})
    public void activateInfo(String category) {
        LogManager.getLogger(category).setLevel(Level.INFO);
    }

    @ManagedOperation(description="Activate debug level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j category to set")})
    public void activateDebug(String category) {
        LogManager.getLogger(category).setLevel(Level.DEBUG);
    }

    @ManagedOperation(description="Activate warn level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j category to set")})
    public void activateWarn(String category) {
        LogManager.getLogger(category).setLevel(Level.WARN);
    }

    @ManagedOperation(description="Activate error level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j category to set")})
    public void activateError(String category) {
        LogManager.getLogger(category).setLevel(Level.ERROR);
    }

    @ManagedOperation(description="Activate fatal level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j category to set")})
    public void activateFatal(String category) {
        LogManager.getLogger(category).setLevel(Level.FATAL);
    }

    @ManagedOperation(description="Retrieve the category log level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j category")})
    public String getLevel(String category) {
        return LogManager.getLogger(category).getLevel().toString();
    }

}
