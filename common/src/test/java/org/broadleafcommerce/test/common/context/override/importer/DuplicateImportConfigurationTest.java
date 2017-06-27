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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This test is showing that you CAN use an annotation with @Import and then use @Import on the Configuration class the
 * annotation is being applied to, BUT the @Import does NOT override the Beans defined in the annotation's Import.
 *
 * @author Nick Crum ncrum
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = DuplicateImportConfigurationTest.DuplicateImportConfiguration.class)
public class DuplicateImportConfigurationTest {

    @Configuration
    @Import(OtherConfiguration.class)
    @EnableAutoConfigurationForImportTest
    static class DuplicateImportConfiguration {}

    static class OtherConfiguration {

        /**
         * This will fail to override the blPasswordEncoder that is imported via the EnableAutoConfigurationForImportTest
         */
        @Bean
        public PasswordEncoder blPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public ObjectMapper blObjectMapper() {
            return new ObjectMapper();
        }
    }

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    public void testDuplicateImport() {
        Assert.assertEquals(ObjectMapper.class, objectMapper.getClass());
        Assert.assertEquals(BCryptPasswordEncoder.class, passwordEncoder.getClass());
    }
}
