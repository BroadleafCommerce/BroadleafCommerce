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

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

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
 * Where the [environment] is a key that is resolved from the current Spring profile. Given that set of properties, they get ordered in the following way:
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
 * If you are using Spring boot, this is a deprecated construct and instead you should rely on application.properties and the profile-specific variants of that. Registration
 * is the same as {@link FrameworkCommonPropertySource}.
 * 
 * <p>
 * Properties registered via this {@link ProfileAwarePropertySource} is functionally equivalent to registering them via {@link PropertySource} and have the same ordering
 * semantics as
 * <a href="http://docs.spring.io/autorepo/docs/spring-boot/current/reference/html/boot-features-external-config.html#boot-features-external-config">"{@literal @}PropertySource annotations on your {@literal @}Configuration classes</a>.
 * The exception is that these are also guaranteed to take a higher precedence than {@link FrameworkCommonPropertySource}.
 * 
 * <p>
 * There is generally only a single one of these beans registered in the application. If multiple of them are registered, they are ordered based on
 * {@link #getOrder()} and <i>all</i> properties are added to the {@link Environment} prior to moving to the next {@link ProfileAwarePropertySource}.
 * 
 * <p>
 * These are guaranteed to take a higher precedence (and thus override) properties registered via {@link FrameworkCommonPropertySource}
 * 
 * <p>
 * These bean instances are created very early in the Spring lifecycle, during {@link BeanFactoryPostProcessor} instantiation. Therefore
 * you should not be doing any complicated bean logic here (instantating dependencies, trying to {@literal @}Autowire anything, etc) or
 * else you might run into unintended consequences.
 *  
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link ProfileAwarePropertiesBeanFactoryPostProcessor}
 */
public class ProfileAwarePropertySource implements Ordered {

    public static final int DEFAULT_ORDER = 100;
    
    protected String classpathFolder;
    
    public ProfileAwarePropertySource(String classpathFolder) {
        this.classpathFolder = classpathFolder;
    }
    
    public String getClasspathFolder() {
        return classpathFolder;
    }

    /**
     * Semantics of this ordering are slightly different than others. In this case, a higher order
     * means that this is registered later within {@link ProfileAwarePropertiesBeanFactoryPostProcessor} which means
     * that it has a higher resolution precedence.
     */
    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }
    
}
