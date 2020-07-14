/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2020 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.config;

import org.broadleafcommerce.common.extensibility.cache.JCacheRegionConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for defining cache regions via Java config when either not using ehcache or when jcache.create.cache.forceJavaConfig is true
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
@Configuration
public class OpenAdminCacheConfig {

    @Bean
    public JCacheRegionConfiguration blAdminSecurityQuery() {
        return new JCacheRegionConfiguration("blAdminSecurityQuery", 86400, 1000);
    }

    @Bean
    public JCacheRegionConfiguration blAdminSecurityQueryVolatile() {
        return new JCacheRegionConfiguration("blAdminSecurityQueryVolatile", 60, 200);
    }

    @Bean
    public JCacheRegionConfiguration blAdminSecurity() {
        return new JCacheRegionConfiguration("blAdminSecurity", 86400, 1000);
    }

    @Bean
    public JCacheRegionConfiguration blAdminSecurityVolatile() {
        return new JCacheRegionConfiguration("blAdminSecurityVolatile", 60, 200);
    }
}
