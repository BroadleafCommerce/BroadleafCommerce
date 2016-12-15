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
/**
 * 
 */
package org.broadleafcommerce.common.context.override;

import org.broadleafcommerce.common.context.override.ConfigurationClassOverrideTest.OverrideConfigClass;
import org.broadleafcommerce.common.extensibility.FrameworkXmlBeanDefinitionReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests whether or not bean overriding works when beans are read via the {@link FrameworkXmlBeanDefinitionReader} and then overridden
 * via an {@literal @}Configuration class.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OverrideConfigClass.class)
public class ConfigurationClassOverrideTest {

    @Configuration
    @ImportResource(value = "classpath:bean-override-test-applicationContext.xml", reader = FrameworkXmlBeanDefinitionReader.class)
    public static class OverrideConfigClass {
        @Bean
        public PasswordEncoder blPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
    
    @Autowired
    protected PasswordEncoder passwordEncoder;
    
    @Test
    public void testOverrideInjection() {
        Assert.assertEquals(BCryptPasswordEncoder.class.getName(), passwordEncoder.getClass().getName());
    }
}
