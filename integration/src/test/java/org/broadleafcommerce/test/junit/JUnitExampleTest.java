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
package org.broadleafcommerce.test.junit;

import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.test.config.BroadleafSiteIntegrationTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import jakarta.annotation.Resource;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@BroadleafSiteIntegrationTest
//@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
public class JUnitExampleTest {
    
    @Resource
    private CatalogService catalogService;
    
    @Test
    public void testInjectionWorks() {
        Assert.assertNotEquals(catalogService, null);
    }
}
