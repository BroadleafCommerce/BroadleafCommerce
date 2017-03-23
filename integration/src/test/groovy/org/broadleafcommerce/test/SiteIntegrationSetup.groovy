/*
 * #%L
 * BroadleafCommerce Custom Field
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
package org.broadleafcommerce.test

import org.broadleafcommerce.common.config.BroadleafEnvironmentConfiguringApplicationListener
import org.broadleafcommerce.common.config.EnableBroadleafSiteRootAutoConfiguration
import org.broadleafcommerce.common.extensibility.FrameworkXmlBeanDefinitionReader
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration

import spock.lang.Specification


/**
 * Base Integration Test Setup groovy file for Site based integration tests. This base class has all the
 * applicationContext's shared by Integration tests for Site based testing. Extend from this class on a
 * per project basis with another setup file that contains only an @ContextHeirarchy(@ContextConfiguration)
 * that references this "siteContexts" ContextConfiguration and add only the contexts, in the locations
 * parameter, that you need to run your tests at that level. Then extend off of that setup file with your
 * actual integration tests. IntegrationSetup files should not have any code in their body's.
 * 
 * @author austinrooke
 *
 */
@Rollback
@ContextConfiguration(name="siteRoot", initializers = BroadleafEnvironmentConfiguringApplicationListener)
@WebAppConfiguration
@ActiveProfiles("mbeansdisabled")
class SiteIntegrationSetup extends Specification {
    
    /**
     * This is a nested configuration class so that you can do a mix of both {@link @}Configuration classes
     * as well as XML configuration files at the same level of the 'siteRoot' {@link @}ContextConfiguration
     */
    @Configuration
    @EnableBroadleafSiteRootAutoConfiguration
    @ImportResource(value = [
            "classpath:bl-applicationContext-test-security.xml",
            "classpath:bl-applicationContext-test.xml"
    ])
    @ComponentScan(["org.broadleafcommerce.profile.web.controller", "org.broadleafcommerce.profile.web.core.service.login"])
    static class ContextConfig {}
    
}
