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
package org.broadleafcommerce.common.util;

import org.springframework.util.Log4jConfigurer;

import java.io.FileNotFoundException;

/**
 * @author Jeff Fischer
 * @deprecated This was originally intended to override existing log4j with a modified configuration file for load testing. 
 * We no longer use Log4J as the default and Spring has deprecated Log4jConfigurer in favor of using a method compatible with Log4J2. 
 * If the functionality provided by this class is required, that should be done as a customization.
 */
@Deprecated
public class RuntimeLog4jConfigurer {

    private String log4jConfigLocation;

    public String getLog4jConfigLocation() {
        return log4jConfigLocation;
    }

    public void setLog4jConfigLocation(String log4jConfigLocation) {
        this.log4jConfigLocation = log4jConfigLocation;
        try {
            Log4jConfigurer.initLogging(log4jConfigLocation);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
