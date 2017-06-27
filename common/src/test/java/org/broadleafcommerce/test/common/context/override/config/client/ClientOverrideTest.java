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
package org.broadleafcommerce.test.common.context.override.config.client;

import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.extensibility.FrameworkXmlBeanDefinitionReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Nick Crum ncrum
 */
@RunWith(SpringRunner.class)
public class ClientOverrideTest {

    @Configuration
    @Import(MainRootConfig.class)
    public static class MainConfiguration {

        @Configuration
        @ImportResource(value = "classpath:context/config/client-override.xml", reader = FrameworkXmlBeanDefinitionReader.class)
        public static class FrameworkConfig {}
    }

    @Autowired
    protected EmailInfo emailInfo;

    @Test
    public void testOverride() {
        Assert.assertEquals("client", emailInfo.getFromAddress());
    }
}
