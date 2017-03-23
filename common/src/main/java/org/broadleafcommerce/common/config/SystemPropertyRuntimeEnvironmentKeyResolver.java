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
package org.broadleafcommerce.common.config;

/**
 * Determines the current runtime environment by reading a system property
 * (specified in environmentKey); if no system property is specified, a
 * (reasonable) default of "runtime.environment" is used.
 * @deprecated Instead of using anything around the -Druntime-environment values, you should be using Spring profiles
 * and properties activated with that via {@link BroadleafEnvironmentConfiguringApplicationListener}.
 */
@Deprecated
public class SystemPropertyRuntimeEnvironmentKeyResolver implements RuntimeEnvironmentKeyResolver {

    protected String environmentKey = "runtime.environment";

    public SystemPropertyRuntimeEnvironmentKeyResolver() {
        // EMPTY
    }

    @Override
    public String resolveRuntimeEnvironmentKey() {
        return System.getProperty(environmentKey);
    }

    public void setEnvironmentKey(String environmentKey) {
        this.environmentKey = environmentKey;
    }
}
