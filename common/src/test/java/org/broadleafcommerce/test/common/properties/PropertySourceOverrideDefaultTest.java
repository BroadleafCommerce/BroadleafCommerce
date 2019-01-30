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
package org.broadleafcommerce.test.common.properties;

import org.broadleafcommerce.common.config.BroadleafEnvironmentConfiguringApplicationListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests to ensure that @TestPropertySource actually overrides anything from the default Broadleaf properties with a negative test
 * to ensure that the {@link @PropertySource} does <i>not</i> override any of the Broadleaf property sources.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = BroadleafEnvironmentConfiguringApplicationListener.class)
@TestPropertySource(properties = "dev.only.property=overridevalue")
public class PropertySourceOverrideDefaultTest {

    @PropertySource("classpath:overridestest.properties")
    @Configuration
    public static class Config { }
    
    @Autowired
    Environment env;
    
    @Test
    public void propertySourceAnnotationDoNotOverrideBroadleafDefaults() {
        Assert.assertNotEquals("overridevalue", env.getProperty("test.property.source"));
    }
    
    @Test
    public void testPropertySourceAnnotationOverridesBroadleafDeafults() {
        // This is something only set in development.properties (the highest profile-specific) but @TestPropertySource should override it
        Assert.assertEquals("overridevalue", env.getProperty("dev.only.property"));
    }
}
