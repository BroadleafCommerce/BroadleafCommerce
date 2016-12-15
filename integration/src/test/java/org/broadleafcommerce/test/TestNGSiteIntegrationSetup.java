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
package org.broadleafcommerce.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Base Integration Test Setup java file for Site based integration tests. This base class has all the
 * applicationContext's shared by Integration tests for Site based testing. Extend from this class on a
 * per project basis with another setup file that contains only an @ContextHierarchy(@ContextConfiguration)
 * that references this "siteContexts" ContextConfiguration and add only the contexts, in the locations
 * parameter, that you need to run your tests at that level. Then extend off of that setup file with your
 * actual integration tests. IntegrationSetup files should not have any code in their body's.
 *
 * @author Jeff Fischer
 *
 */
@Rollback
@ContextConfiguration(name="siteRoot")
@WebAppConfiguration
@ActiveProfiles("mbeansdisabled")
@TestExecutionListeners(TransactionalTestExecutionListener.class)
public class TestNGSiteIntegrationSetup extends AbstractTestNGSpringContextTests {
    
    /**
     * This is a nested configuration class so that you can do a mix of both {@link @}Configuration classes
     * as well as XML configuration files at the same level of the 'siteRoot' {@link @}ContextConfiguration
     */
    @Configuration
    @ImportResource({"classpath*:/blc-config/site/bl-*-applicationContext.xml",
            "classpath:bl-applicationContext-test-security.xml",
            "classpath:bl-applicationContext-test.xml"
        })
    @ComponentScan({"org.broadleafcommerce.profile.web.controller", "org.broadleafcommerce.profile.web.core.service.login"})
    public static class ContextConfig {}
}
