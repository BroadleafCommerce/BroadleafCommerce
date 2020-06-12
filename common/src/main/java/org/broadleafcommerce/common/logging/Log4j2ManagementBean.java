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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * This is not hooked up by default so that Log4j2 is not required. If you are using Log4j2, you can add this class to your
 * Spring applicationContext to enable it.
 *
 * @author Nathan Moore (nathanmoore).
 */
@ManagedResource(objectName="org.broadleafcommerce:name=Log4J2Manangement", description="Logging Management", currencyTimeLimit=15)
public class Log4j2ManagementBean {

    @ManagedOperation(description="Activate info level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j2 category to set")})
    public void activateInfo(String category) {
        Configurator.setLevel(category, Level.INFO);
    }

    @ManagedOperation(description="Activate debug level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j2 category to set")})
    public void activateDebug(String category) {
        Configurator.setLevel(category, Level.DEBUG);
    }

    @ManagedOperation(description="Activate warn level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j2 category to set")})
    public void activateWarn(String category) {
        Configurator.setLevel(category, Level.WARN);
    }

    @ManagedOperation(description="Activate error level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j2 category to set")})
    public void activateError(String category) {
        Configurator.setLevel(category, Level.ERROR);
    }

    @ManagedOperation(description="Activate fatal level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j2 category to set")})
    public void activateFatal(String category) {
        Configurator.setLevel(category, Level.FATAL);
    }

    @ManagedOperation(description="Retrieve the category log level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j2 category")})
    public String getLevel(String category) {
        return LogManager.getLogger(category).getLevel().toString();
    }

}
