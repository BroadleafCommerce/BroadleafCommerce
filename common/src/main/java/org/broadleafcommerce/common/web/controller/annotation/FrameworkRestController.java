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

import org.broadleafcommerce.common.web.controller.FrameworkControllerHandlerMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated class is a "Framework REST Controller" (default RESTful controller).
 * <p>
 * This means that if {@link EnableFrameworkRestControllers} or {@link EnableAllFrameworkControllers} is included in the
 * application configuration then classes annotated with {@link FrameworkRestController} will be component scanned and
 * included in the application context and that {@link FrameworkMapping}s will be added to handler mappings with a lower
 * priority than {@link org.springframework.web.bind.annotation.RequestMapping}s found within a class annotated with
 * {@link org.springframework.web.bind.annotation.RestController}. This priority is achieved through {@link
 * FrameworkControllerHandlerMapping} having a higher order value than {@link RequestMappingHandlerMapping}.
 * <p>
 * The intention is that you are able to specify RESTful controllers and mappings within a framework module as the
 * default REST endpoints and a client application can essentially override those mappings without causing an ambiguous
 * mapping exception.
 * <p>
 * The site handler mappings in play in order of precedence from highest to lowest are:
 * <ol>
 * <li>{@link RequestMappingHandlerMapping}</li>
 * <li>{@link FrameworkControllerHandlerMapping}</li>
 * </ol>
 * <p>
 * The admin handler mappings in play in order of precedence from highest to lowest are:
 * <ol>
 * <li>{@link AdminRequestMappingHandlerMapping}</li>
 * <li>{@link FrameworkControllerHandlerMapping}</li>
 * <li>{@link AdminControllerHandlerMapping}</li>
 * </ol>
 *
 * @author Philip Baggett (pbaggett)
 * @see FrameworkMapping
 * @see EnableFrameworkRestControllers
 * @see EnableAllFrameworkControllers
 * @since 5.2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ResponseBody
public @interface FrameworkRestController {
}
