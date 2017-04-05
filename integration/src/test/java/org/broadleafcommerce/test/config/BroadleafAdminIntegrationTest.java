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

import org.broadleafcommerce.common.config.BroadleafEnvironmentConfiguringApplicationListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Convenient annotation for integration tests dealing with the Broadleaf Admin applicationContext. This can be used to annotate
 * any type of test that uses spring-test (e.g. TestNG, Spock, JUnit).
 * 
 * <p>
 * Customization of the Spring ApplicationContext that this creates follows the same rules laid out in {@link ContextConfiguration}. Subclasses
 * should target the {@code "adminRoot"} context name in their {@link ContextHierarchy}.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ContextConfiguration(name = "adminRoot",
    initializers = BroadleafEnvironmentConfiguringApplicationListener.class,
    classes = AdminTestContextConfiguration.class)
@WebAppConfiguration
@ActiveProfiles("mbeansdisabled")
public @interface BroadleafAdminIntegrationTest {

}
