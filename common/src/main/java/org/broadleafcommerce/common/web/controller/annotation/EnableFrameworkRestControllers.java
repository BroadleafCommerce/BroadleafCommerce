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

import org.broadleafcommerce.common.config.BroadleafBeanNameGenerator;
import org.broadleafcommerce.common.web.controller.FrameworkControllerHandlerMapping;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables only {@link FrameworkRestController} annotations, which are the RESTful controllers.
 * <p>
 * If you desire all of Broadleaf's default controllers, including the MVC ones, then use {@link
 * EnableAllFrameworkControllers} instead.
 * <p>
 * Scan all Broadleaf modules for {@link FrameworkRestController} so that their {@link FrameworkMapping}s will get
 * included in {@link FrameworkControllerHandlerMapping} to provide default implementations of web endpoints.
 * <p>
 * If only some {@link FrameworkRestController}s are desired, then use {@link #excludeFilters()} to disable undesired
 * default controllers.
 * <p>
 * <b>DO NOT place this annotation on the same class as another {@link ComponentScan} or other annotations that compose
 * {@link ComponentScan} such as {@code @SpringBootApplication} and {@link EnableFrameworkControllers} as they will
 * conflict when Spring performs annotation composition.</b> Instead, you can create a nested class in your
 * {@code @SprintBootApplication} class like this:
 * <pre>
 * {@code
 * @literal @SpringBootApplication
 * public class MyApplication {
 *
 *     @literal @EnableFrameworkRestControllers
 *     public static class EnableBroadleafRestControllers {}
 *
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApplication.class, args);
 *     }
 * }
 * }
 * </pre>
 *
 * @author Philip Baggett (pbaggett)
 * @see FrameworkRestController
 * @see FrameworkMapping
 * @see FrameworkControllerHandlerMapping
 * @since 5.2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan(
        useDefaultFilters = false,
        basePackages = {"org.broadleafcommerce", "com.broadleafcommerce"},
        includeFilters = @Filter({FrameworkRestController.class}),
        nameGenerator = BroadleafBeanNameGenerator.class)
public @interface EnableFrameworkRestControllers {

    /**
     * A set of {@link Filter}s that describe classes to exclude from component scanning.
     * <p>
     * This is most useful when you want to enable some framework controllers but exclude others. You can exclude
     * classes annotated with {@link FrameworkRestController} by providing a filter like {@code
     * @EnableFrameworkRestControllers(excludeFilters = @Filter(value = DefaultCustomerRestController.class, type =
     * FilterType.ASSIGNABLE_TYPE))}
     *
     * @see ComponentScan#excludeFilters()
     * @see Filter
     */
    @AliasFor(annotation = ComponentScan.class, attribute = "excludeFilters")
    Filter[] excludeFilters() default {};
}
