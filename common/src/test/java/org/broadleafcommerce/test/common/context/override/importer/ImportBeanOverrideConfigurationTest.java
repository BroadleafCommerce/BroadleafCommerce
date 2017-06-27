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
package org.broadleafcommerce.test.common.context.override.importer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * This test is intended to test how to override framework bean definitions with a client-level configuration class.
 * In this case the best way to achieve this is to create an inner static Configuration class that defines any bean
 * overrides that need to be prioritized. Then, due to the FrameworkXMLBeanDefinitionReader the beans defined by the
 * inner static class would be prioritized above the beans registered in any @Import or @ImportResource on the outer class.
 *
 * @author Nick Crum ncrum
 */
@RunWith(SpringRunner.class)
public class ImportBeanOverrideConfigurationTest {

    @Configuration
    @EnableAutoConfigurationForImportTest
    static class RootBeanOverrideConfiguration {

        @Configuration
        static class BeanOverrideConfiguration {

            @Bean
            public PasswordEncoder blPasswordEncoder() {
                return new BCryptPasswordEncoder();
            }
        }
    }

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Test
    public void testBeanOverride() {
        Assert.assertEquals(BCryptPasswordEncoder.class, passwordEncoder.getClass());
    }
}
