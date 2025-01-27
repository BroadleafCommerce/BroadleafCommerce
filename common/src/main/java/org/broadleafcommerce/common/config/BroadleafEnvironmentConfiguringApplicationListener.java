/*-
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * <p>
 * In non-boot this class should be hooked up in your web.xml as shown below
 *
 * <pre>
 * {@literal
 * <context-param>
 *   <param-name>contextInitializerClasses</param-name>
 *   <param-value>org.broadleafcommerce.common.config.BroadleafEnvironmentConfiguringApplicationListener</param-value>
 * </context-param>
 * }
 * </pre>
 * <p>
 * For Spring Boot deployments see {@link org.broadleafcommerce.common.config.BroadleafEnvironmentConfiguringPostProcessor}
 *
 * @author Jeff Fischer
 * @author Phillip Verheyden (phillipuniverse)
 * @see BroadleafEnvironmentConfigurer
 * @since 5.2
 */
public class BroadleafEnvironmentConfiguringApplicationListener extends BroadleafEnvironmentConfigurer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        configure(applicationContext.getEnvironment());
    }

}
