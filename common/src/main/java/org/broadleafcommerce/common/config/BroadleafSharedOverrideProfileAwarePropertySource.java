/*
 * #%L
 * BroadleafCommerce Common Libraries
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
/**
 * 
 */
package org.broadleafcommerce.common.config;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

/**
 * <p>
 * Holder for a folder of properties resources comprised of something like the following on the classpath:
 * 
 * <ul>
 *   <li>classpath:/runtime-properties/</li>
 *   <ul>
 *     <li>common.properties</li>
 *     <li>common-shared.properties</li>
 *     <li>[profile].properties</li>
 *     <li>[profile]-shared.properties</li>
 *   </ul>
 * </ul>
 * 
 * <p>
 * Where the [profile] is a key that is resolved from the current Spring profile. Given that set of properties, they get ordered in the following way, where later files
 * are applied after earlier ones and thus take precedence:
 * 
 * <ul>
 *   <li>classpath:/runtime-properties/</li>
 *   <ol>
 *     <li>common-shared.properties</li>
 *     <li>common.properties</li>
 *     <li>[profile]-shared.properties</li>
 *     <li>[profile].properties</li>
 *   </ol>
 * </ul>
 *  
 * <p>
 * Note that this is largely created to support existing Broadleaf implementations for versions older than 5.2. Assuming that you are on Spring Boot, you should look to
 * use {@code application.properties} variants from <a href="http://docs.spring.io/autorepo/docs/spring-boot/current/reference/html/boot-features-external-config.html#boot-features-external-config">
 * the Spring Boot documentation</a>
 * 
 * <p>
 * There is generally only a single one of these sources registered in the application, the {@link DefaultRuntimeEnvironmentProfileAwarePropertySource}. If multiple of them
 * are registered, they are ordered based on {@link #getOrder()} and <i>all</i> properties are added to the {@link Environment} prior to moving to the next
 * {@link BroadleafSharedOverrideProfileAwarePropertySource}. To register your own version (again, not necessarily recommended) add this as an entry in a {@code META-INF/spring.factories} file:
 * 
 * <pre>
 * org.broadleafcommerce.common.config.BroadleafSharedOverrideProfileAwarePropertySource=org.broadleafcommerce.common.config.SomeCustomizedProfileAwarePropertySource
 * </pre>
 * 
 * <p>
 * Properties registered via this {@link BroadleafSharedOverrideProfileAwarePropertySource} is functionally equivalent to registering them via {@link PropertySource} and have the same ordering
 * semantics as
 * <a href="http://docs.spring.io/autorepo/docs/spring-boot/current/reference/html/boot-features-external-config.html#boot-features-external-config">"{@literal @}PropertySource annotations on your {@literal @}Configuration classes</a>.
 * An important distinction is that these sources <i>have a higher priority</i> than any {@link PropertySource} annotations on {@literal @}Configuration classes.
 * 
 * <p>
 * These are guaranteed to take a higher precedence (and thus override) properties registered via {@link FrameworkCommonClasspathPropertySource}
 *  
 * @author Phillip Verheyden (phillipuniverse)
 * @since 5.2
 * @see {@link DefaultRuntimeEnvironmentProfileAwarePropertySource}
 * @see {@link BroadleafEnvironmentConfiguringApplicationListener}
 * @see {@link FrameworkCommonClasspathPropertySource}
 */
public interface BroadleafSharedOverrideProfileAwarePropertySource {

    public static final int DEFAULT_ORDER = 100;
    
    /**
     * The folder on the classpath that contains a {@code common.properties} file. Note that this cannot be prefixed with {@code "classpath:"} or any of those
     * varieties since this drives the creation of an {@link ClassPathResource} already based on this location.
     */
    public String getClasspathFolder();

}
