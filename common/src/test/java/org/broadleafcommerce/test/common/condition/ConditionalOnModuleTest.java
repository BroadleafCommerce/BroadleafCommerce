/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.test.common.condition;

import org.broadleafcommerce.common.condition.ConditionalOnBroadleafModule;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@RunWith(SpringRunner.class)
public class ConditionalOnModuleTest {

    @Configuration
    @ConditionalOnBroadleafModule(moduleName = CommonTestRegistration.COMMON_TEST_REGISTRATION_NAME)
    public static class ConfigurationClassTest {
        
        @Bean
        protected String configurationBeanLoaded() {
            return "Loaded";
        }
    }
    
    @Configuration
    public static class TestBeanConfig {

        @Bean
        @ConditionalOnBroadleafModule(moduleName = CommonTestRegistration.COMMON_TEST_REGISTRATION_NAME)
        protected String beanMethodLoaded() {
            return "Loaded";
        }        
        
        @Bean
        @ConditionalOnBroadleafModule(moduleName = "Random Module Name")
        protected String notLoaded() {
            return "Not Loaded";
        }
    }
    
    @Autowired
    protected ApplicationContext appctx;
    
    @Test
    public void testConfigurationClass() {
        Assert.assertNotNull(appctx.getBean("configurationBeanLoaded"));
    }
    
    @Test
    public void testBeanMethod() {
        Assert.assertNotNull(appctx.getBean("beanMethodLoaded"));
    }
    
    @Test(expected = NoSuchBeanDefinitionException.class)
    public void testNotLoaded() {
        appctx.getBean("notLoaded");
    }
    
}
