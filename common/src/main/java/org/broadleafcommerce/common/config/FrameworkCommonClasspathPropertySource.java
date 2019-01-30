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
package org.broadleafcommerce.common.config;

import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

/**
 * <p>
 * A holder for a folder containing a {@code common.properties} file to be added to the Spring {@link Environment} in a programmatic way without Spring Boot and with very specific
 * ordering semantics. This is generally only used within the Broadleaf Framework and supporting modules. This assumes that the location is on the
 * classpath as this drives the creation of a {@link ClassPathResource}. To write your custom one of these, implementations should add an entry to
 * {@code META-INF/spring.factories} like:
 * 
 * <pre>
 * org.broadleafcommerce.common.config.FrameworkCommonClasspathPropertySource=org.broadleafcommerce.common.config.BroadleafCommonPropertySource
 * </pre>
 * 
 * <p>
 * Any properties resolved by these classes have a lower precedence than any {@link BroadleafSharedOverrideProfileAwarePropertySource}. However, these have a higher
 * precedence than any {@literal @}PropertySource annotations. Given the ordering mentioned in <a href="http://docs.spring.io/autorepo/docs/spring-boot/current/reference/html/boot-features-external-config.html#boot-features-external-config">this Spring Boot doc</a>
 * properties resolved by these classes come immediately before the {@literal @}PropertySource annotations.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @since 5.2
 * @see {@link DefaultOrderFrameworkCommonClasspathPropertySource}
 * @see {@link BroadleafSharedOverrideProfileAwarePropertySource}
 * @see {@link BroadleafEnvironmentConfiguringApplicationListener}
 */
public interface FrameworkCommonClasspathPropertySource {

    /**
     * All of the property configurations registered by the core framework (e.g. broadleaf-framework, broadleaf-common, broadleaf-open-admin-platform, etc)
     * have this ordering.
     * 
     * This ordering is mainly for backwards-compatibilty's sake, as this was the ordering essentially defined in the old StandardConfigLocations.txt
     */
    public static final int BROADLEAF_COMMON_ORDER = -8000;
    public static final int PROFILE_ORDER = -7000;
    public static final int PROFILE_WEB_ORDER = -6000;
    public static final int FRAMEWORK_ORDER = -5000;
    public static final int FRAMEWORK_WEB_ORDER = -4000;
    public static final int OPEN_ADMIN_ORDER = -3000;
    public static final int ADMIN_MODULE_ORDER = -2000;
    public static final int CMS_ORDER = -1000;
    
    public static final int DEFAULT_ORDER = 1000;
    
    /**
     * A folder on the classpath that contains a {@code common.properties} file. Note that this cannot be prefixed with {@code "classpath:"} or any of those
     * varieties since this drives the creation of an {@link ClassPathResource} already based on this location.
     */
    String getClasspathFolder();
    
}
