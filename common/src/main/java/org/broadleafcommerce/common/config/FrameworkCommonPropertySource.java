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
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

/**
 * <p>
 * A holder for common.properties location. This assumes that this is from the current classpath as this drives
 * the creation of a {@link ClassPathResource}. Example:
 * 
 * <pre>
 * {@literal @}Configuration
 * public class ModuleProperties {
 *     {@literal @}Bean
 *     public static FrameworkCommonPropertySource blModuleProps() {
 *         return new FrameworkCommonPropertySource("config/bc/module");
 *     }
 * }
 * 
 * <p>
 * This is used to derive special meaning in the {@link ProfileAwarePropertiesBeanFactoryPostProcessor} where they are
 * added to the active Spring {@link Environment}.
 * 
 * <p>
 * Any properties resolved by these classes have a lower precedence than any {@link ProfileAwarePropertySource}.
 * 
 * <p>
 * These bean instances are created very early in the Spring lifecycle, during {@link BeanFactoryPostProcessor} instantiation. Therefore
 * you should not be doing any complicated bean logic here (instantating dependencies, trying to {@literal @}Autowire anything, etc) or
 * else you might run into unintended consequences. Also note that the registration of this {@literal @}Bean method is {@code static} since
 * it should follow the same registration rules of a {@link BeanFactoryPostProcessor}.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link ProfileAwarePropertiesBeanFactoryPostProcessor}
 * @see {@link ProfileAwarePropertySource}
 */
public class FrameworkCommonPropertySource implements Ordered {

    public static final int DEFAULT_ORDER = 1000;
    
    protected String classpathFolder;
    protected int order = 1000;
    
    public FrameworkCommonPropertySource(String classpathFolder) {
        this.classpathFolder = classpathFolder;
    }
    
    public FrameworkCommonPropertySource(String classpathFolder, int order) {
        this.classpathFolder = classpathFolder;
        this.order = order;
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
        return order;
    }
}
