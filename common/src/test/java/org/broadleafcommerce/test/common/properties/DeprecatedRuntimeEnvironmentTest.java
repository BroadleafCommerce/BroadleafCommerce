/**
 * 
 */
package org.broadleafcommerce.test.common.properties;

import org.broadleafcommerce.common.config.ProfileAwarePropertiesBeanFactoryPostProcessor;
import org.broadleafcommerce.test.common.properties.DefaultDevelopmentOverridePropertiesTest.PropertyTestConfig;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PropertyTestConfig.class)
public class DeprecatedRuntimeEnvironmentTest {

    @BeforeClass
    public static void setRuntimeEnvironment() {
        System.setProperty(ProfileAwarePropertiesBeanFactoryPostProcessor.DEPRECATED_RUNTIME_ENVIRONMENT_KEY, "production");
    }
    
    @AfterClass
    public static void clearRuntimeEnvironment() {
        System.clearProperty(ProfileAwarePropertiesBeanFactoryPostProcessor.DEPRECATED_RUNTIME_ENVIRONMENT_KEY);
    }
    
    @Autowired
    protected Environment env;
    
    @Test
    public void testDeprecatedRuntimeEnvironmentKey() {
        Assert.assertEquals("productionvalue", env.getProperty(PropertyTestConfig.TEST_PROPERTY));
    }
}
