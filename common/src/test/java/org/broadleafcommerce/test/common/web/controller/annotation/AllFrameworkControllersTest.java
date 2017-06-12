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
package org.broadleafcommerce.test.common.web.controller.annotation;

import org.broadleafcommerce.common.web.controller.annotation.EnableAllFrameworkControllers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests fomr {@link EnableAllFrameworkControllers}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@RunWith(SpringRunner.class)
public class AllFrameworkControllersTest {

    @Configuration
    @EnableAllFrameworkControllers
    static class Config {}
    
    @Autowired
    private ApplicationContext appctx;
    
    @Test
    public void frameworkControllerPrefixed() {
        Assert.assertEquals("blTestFrameworkController", appctx.getBeanNamesForType(TestFrameworkController.class)[0]);
        Assert.assertEquals("blTestFrameworkRestController", appctx.getBeanNamesForType(TestFrameworkRestController.class)[0]);
    }
    
}
