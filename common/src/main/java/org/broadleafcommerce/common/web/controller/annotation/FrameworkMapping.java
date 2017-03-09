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
package org.broadleafcommerce.common.web.controller.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation behaves exactly like {@link RequestMapping} except it is used inside {@link FrameworkController} and
 * {@link FrameworkRestController} controllers.
 * <p>
 * This reasoning for this annotation instead of just using {@link RequestMapping} is that when framework controllers
 * haven't been enabled and a framework controller is extended by a class annotated with {@link
 * org.springframework.stereotype.Controller} or {@link org.springframework.web.bind.annotation.RestController} then the
 * undesired {@link RequestMapping}s will get picked up once again due to Spring's annotation inheritance mechanics.
 *
 * @see RequestMapping
 * @see FrameworkController
 * @see FrameworkRestController
 * @since 5.2
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FrameworkMapping {

    /**
     * @see RequestMapping#name()
     */
    String name() default "";

    /**
     * @see RequestMapping#value()
     */
    @AliasFor("path")
    String[] value() default {};

    /**
     * @see RequestMapping#path()
     */
    @AliasFor("value")
    String[] path() default {};

    /**
     * @see RequestMapping#method()
     */
    RequestMethod[] method() default {};

    /**
     * @see RequestMapping#params()
     */
    String[] params() default {};

    /**
     * @see RequestMapping#headers()
     */
    String[] headers() default {};

    /**
     * @see RequestMapping#consumes()
     */
    String[] consumes() default {};

    /**
     * @see RequestMapping#produces()
     */
    String[] produces() default {};
}
