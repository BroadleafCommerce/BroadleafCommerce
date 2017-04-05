/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.test.config;

import org.broadleafcommerce.common.config.EnableBroadleafSiteRootAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Configuration class holder for all of the configuration for scanning all Broadleaf non-servlet beans for an integration test.
 * This particular class can be used for composing your own context configuration but generally this class is not used directly.
 * Instead see {@link BroadleafSiteIntegrationTest} which makes use of this configuration to initialize a Spring ApplicationContext
 * 
 * @see BroadleafSiteIntegrationTest
 * @see EnableBroadleafSiteRootAutoConfiguration
 * @author Phillip Verheyden (phillipuniverse)
 */
@Configuration
@EnableBroadleafSiteRootAutoConfiguration
@ImportResource(value = {
        "classpath:bl-applicationContext-test-security.xml",
        "classpath:bl-applicationContext-test.xml"
    })
@ComponentScan({"org.broadleafcommerce.profile.web.controller", "org.broadleafcommerce.profile.web.core.service.login"})
public class SiteTestContextConfiguration {

}
