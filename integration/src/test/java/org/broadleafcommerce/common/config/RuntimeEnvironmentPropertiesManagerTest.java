/*
 * #%L
 * BroadleafCommerce Integration
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

import org.broadleafcommerce.test.TestNGSiteIntegrationSetup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.Resource;

@ContextHierarchy(@ContextConfiguration(name="siteRoot"))
public class RuntimeEnvironmentPropertiesManagerTest extends TestNGSiteIntegrationSetup {

    @Configuration
    public static class RuntimeEnvironmentPropertiesTestConfig {
        @Bean
        public RuntimeEnvironmentPropertiesManager blConfigurationManager() {
            return new RuntimeEnvironmentPropertiesManager();
        }
    }
    
    @Resource(name = "blConfigurationManager")
    RuntimeEnvironmentPropertiesManager configurationManager;

    @Test
    public void testPropertyOnly() throws Exception {
        String s = configurationManager.getProperty("detect.sequence.generator.inconsistencies");
        if(s.indexOf("$")>=0) {
            Assert.fail("RuntimeEnvironmentPropertiesManager bean not defined");
        }
        
    }
    @Test(dependsOnMethods={"testPropertyOnly"})
    public void testPrefix() throws Exception {
        configurationManager.setPrefix("detect");
        String s = configurationManager.getProperty("sequence.generator.inconsistencies");
        if(s.indexOf("$")>=0) {
            Assert.fail("RuntimeEnvironmentPropertiesManager bean not defined");
        }
    }
    @Test(dependsOnMethods={"testPrefix"})
    public void testSuffix() throws Exception {
        
        String s = configurationManager.getProperty("sequence.generator","inconsistencies");
        if(s.indexOf("$")>=0) {
            Assert.fail("RuntimeEnvironmentPropertiesManager bean not defined");
        }
    }
    @Test(dependsOnMethods={"testSuffix"})
    public void testNullSuffix() throws Exception {
        configurationManager.setPrefix("detect");
        String s = configurationManager.getProperty("sequence.generator.inconsistencies", "SOMETHING");
        Assert.assertNotNull(s);
    }
    @Test
    public void testNULL() throws Exception {

        String s = configurationManager.getProperty(null, "SOMETHING");
  
        Assert.assertEquals(s, null);
    }
}
