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

import org.broadleafcommerce.common.admin.condition.ConditionalOnAdmin;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.lang.reflect.Method;

/**
 * This class registers controllers annotated with {@link AdminFrameworkController} so they will be registered in a
 * separate {@link org.springframework.web.servlet.HandlerMapping} therefore lowering the precedence and allowing client
 * applications to create identical override mappings without causing an ambiguous mapping exception.
 * <p>
 * The handler mappings in play in order of precedence from highest to lowest are:
 * <ol>
 * <li>{@link RequestMappingHandlerMapping}</li>
 * <li>{@link FrameworkControllerHandlerMapping}</li>
 * <li>{@link AdminFrameworkControllerHandlerMapping}</li>
 * </ol>
 *
 * @author Philip Baggett (pbaggett)
 * @see AdminFrameworkController
 */
@Component
@ConditionalOnAdmin
public class AdminFrameworkControllerHandlerMapping extends RequestMappingHandlerMapping {

    public AdminFrameworkControllerHandlerMapping() {
        setOrder(Ordered.LOWEST_PRECEDENCE - 1);
    }

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return beanType.getAnnotation(AdminFrameworkController.class) != null;
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {

        RequestMappingInfo requestMappingInfo = super.getMappingForMethod(method, handlerType);

        if (requestMappingInfo != null) {
            AdminFrameworkController adminFrameworkControllerAnnotation = handlerType.getAnnotation(AdminFrameworkController.class);
            if (adminFrameworkControllerAnnotation != null && adminFrameworkControllerAnnotation.requestMapping().length > 0) {
                RequestMapping requestMappingAnnotation = adminFrameworkControllerAnnotation.requestMapping()[0];
                requestMappingAnnotation = AnnotationUtils.synthesizeAnnotation(requestMappingAnnotation, null);
                RequestMappingInfo parentRequestMappingInfo = createRequestMappingInfo(requestMappingAnnotation, null);
                requestMappingInfo = parentRequestMappingInfo.combine(requestMappingInfo);
                return requestMappingInfo;
            }
        }

        return requestMappingInfo;
    }
}
