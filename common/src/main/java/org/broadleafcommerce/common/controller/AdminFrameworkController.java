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
package org.broadleafcommerce.common.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation specifies that a controller class should be included in the {@link
 * AdminFrameworkControllerHandlerMapping} {@link org.springframework.web.servlet.HandlerMapping} instead of the default
 * {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping}.
 * <p>
 * This results in the {@link RequestMapping} methods of this controller having a lower precedence than ones defined by
 * a class annotated with {@link org.springframework.stereotype.Controller}.
 * <p>
 * Due to {@link RequestMapping} methods being in a separate {@link org.springframework.web.servlet.HandlerMapping}
 * client applications can create identical mappings in their controllers that will override the default method and not
 * cause an ambiguous mapping exception.
 * <p>
 * <b>DO NOT place a {@link RequestMapping} annotation at the class level where this annotation is used.</b> Instead,
 * place the parent {@link RequestMapping} as the {@link #requestMapping()} attribute of this annotation.
 * <p>
 * Note that this annotation does not compose a stereotype that registers the class as a bean. This is done because of
 * the way Broadleaf does bean overriding by ID currently. So bean registration must be done by another means such as
 * adding {@link org.springframework.stereotype.Component} or an XML definition. In other words, this annotation behaves
 * more like {@link RequestMapping} than {@link org.springframework.stereotype.Controller}.
 *
 * @author Philip Baggett (pbaggett)
 * @see AdminFrameworkControllerHandlerMapping
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminFrameworkController {

    /**
     * The parent {@link RequestMapping} to combine with method level {@link RequestMapping} annotations.
     *
     * @see RequestMapping
     */
    RequestMapping[] requestMapping() default {};
}
