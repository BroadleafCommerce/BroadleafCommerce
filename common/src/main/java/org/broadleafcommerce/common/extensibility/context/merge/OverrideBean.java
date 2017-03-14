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
package org.broadleafcommerce.common.extensibility.context.merge;

import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Provides a convenient annotation for declaring an override of a target bean. This addition was made to allow client
 * beans to override framework beans that were defined in XML.
 *
 * @see OverrideBeanAnnotationAwareBeanDefinitionRegistryPostProcessor
 * @author Nick Crum ncrum
 */
@Target(ElementType.METHOD)
@Retention(RUNTIME)
@Bean
public @interface OverrideBean {

    /**
     * Alias for {@link #target()}
     * @see {@link #target()}
     */
    @AliasFor("target")
    String value() default "";

    /**
     * The Spring bean id of the target collection or map to receive the merge
     */
    @AliasFor("value")
    String target() default "";
}
