/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.test.config;

import org.broadleafcommerce.common.config.EnableBroadleafAdminRootAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Configuration class that instantiates all of the Broadleaf Admin beans. This is not generally used outside of the
 * {@link BroadleafAdminIntegrationTest} annotation but it can be used to compose other contexts outside of that
 * annotation.
 * 
 * @see EnableBroadleafAdminRootAutoConfiguration
 * @author Phillip Verheyden (phillipuniverse)
 */
@Configuration
@EnableBroadleafAdminRootAutoConfiguration
@ImportResource(value = {
    "classpath:bl-applicationContext-test.xml"
})
public class AdminTestContextConfiguration {
    
}
