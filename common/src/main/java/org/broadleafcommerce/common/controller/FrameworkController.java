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
package org.broadleafcommerce.common.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated class is a "Framework Controller" (default web controller).
 * <p>
 * This means that if {@link EnableFrameworkControllers} is included in the application configuration then classes
 * annotated with {@link FrameworkController} will be component scanned and included in the application context and that
 * {@link RequestMapping}s will be added to handler mappings with a lower priority than those found within a class
 * annotated with {@link org.springframework.stereotype.Controller}. This priority is achieved through {@link
 * FrameworkControllerHandlerMapping} having a higher order value than {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping}.
 * <p>
 * The intention is that you are able to specify controllers and mappings within a framework module as the default
 * endpoint mappings and a client application can essentially override those mappings without causing an ambiguous
 * mapping exception.
 * <p>
 * <b>Do not use {@link RequestMapping} directly on a class with this annotation, instead pass the {@link
 * RequestMapping} as the {@link #value()} of this annotation.</b>
 * <p>
 * This concept was adapted from {@code @FrameworkEndpoint} from Spring Security OAuth 2.
 *
 * @author Philip Baggett (pbaggett)
 * @see EnableFrameworkControllers
 * @since 5.2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FrameworkController {

    /**
     * Parent {@link RequestMapping} to use while building the path to endpoints.
     * <p>
     * This is the {@link FrameworkController} equivalent to specifying {@link org.springframework.stereotype.Controller}
     * and {@link RequestMapping} together.
     * <p>
     * While this is technically an array, only the first {@link RequestMapping} will be used. Since there is no {@code
     * default null} for annotations, this had to be created as a list in order to have a sane default.
     *
     * @see RequestMapping
     */
    RequestMapping[] value() default {};
}
