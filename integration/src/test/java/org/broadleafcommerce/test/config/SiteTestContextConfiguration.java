/*-
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.test.config;

import org.broadleafcommerce.common.config.EnableBroadleafSiteRootAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

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
    //need this bean because it seems that in spring 6 default request matcher bean
    //is MvcRequestMatcher that requires HandlerMappingIntrospector as constructor arg
    //oob spring knows that bean and requires under the name: mvcHandlerMappingIntrospector
    //By default it is created in WebMvcConfigurationSupport which will be a part of config
    //with mvc autoconfiguration, but for whatever reason we don't use it, but use
    //@WebAppConfiguration in BroadleafSiteIntegrationTest.
    //Basically required because we have bl-applicationContext-test-security.xml, where
    //spring security is configured, specifically <sec:intercept-url pattern="/account/**" access="ROLE_USER" />
    @Bean
    @ConditionalOnMissingBean
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }
}
