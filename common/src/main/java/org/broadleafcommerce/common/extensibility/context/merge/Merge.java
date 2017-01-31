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
package org.broadleafcommerce.common.extensibility.context.merge;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Provides a convenience annotation for declaring a bean merge into an existing collection of beans (presumably a
 * pre-existing Broadleaf Commerce collection).
 * </p>
 * Merges may be declared for a single member, collection or map. Targets may be collections or maps. Please note,
 * if the target is a map, the source element must be a MapFactoryBean. However, if the target is a collection, the source
 * map be a single bean, a ListFactoryBean, or a SetFactoryBean.
 *
 * @author Jeff Fischer
 */
@Target(ElementType.METHOD)
@Retention(RUNTIME)
@Bean
public @interface Merge {

    /**
     * Alias for {@link #targetRef()}
     * @see {@link #targetRef()}
     */
    @AliasFor("targetRef")
    String value() default "";
    
    /**
     * The Spring bean id of the target collection or map to receive the merge
     */
    @AliasFor("value")
    String targetRef() default "";

    /**
     * Specifies how the annotated collection, map or single bean should be inserted into the target collection or map.
     * Default to {@link Placement#APPEND}.
     */
    Placement placement() default Placement.APPEND;

    /**
     * The position in the target map or collection the annotated collection, map or single bean should be inserted. Only
     * meaningful when the {@link #placement()} param is set to {@link Placement#SPECIFIC}.
     */
    int position() default 0;

    /**
     * Specify a class implementing {@link MergeBeanStatusProvider} that can be used to conditionally determine if the
     * merge should take place. The class will be instantiated and executed at runtime during bean post processing.
     *
     * @see MergeBeanStatusProvider#isProcessingEnabled(Object, String, ApplicationContext)
     */
    Class<MergeBeanStatusProvider> statusProvider() default MergeBeanStatusProvider.class;

    /**
     * Whether or not this merge should take place early, before entityManagerFactory configuration. This is useful if the merge
     * should take place before entity processing. Usually declared as true in conjunction with merges that impact
     * entity load time weaving. The default is false.
     */
    boolean early() default false;

}
