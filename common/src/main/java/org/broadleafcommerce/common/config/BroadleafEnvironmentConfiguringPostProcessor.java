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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>
 * This is by default added into {@code META-INF/spring.factories} with the {@link org.springframework.boot.env.EnvironmentPostProcessor} key.
 * In non-boot applications refer to {@link org.broadleafcommerce.common.config.BroadleafEnvironmentConfiguringApplicationListener}
 *
 * @author Jay Aisenbrey (cja769)
 * @see BroadleafEnvironmentConfigurer
 * @since 6.1
 */
public class BroadleafEnvironmentConfiguringPostProcessor extends BroadleafEnvironmentConfigurer implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        configure(environment);
    }

}
