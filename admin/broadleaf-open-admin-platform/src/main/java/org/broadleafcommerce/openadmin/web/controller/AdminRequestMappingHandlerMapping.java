/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.web.controller.FrameworkControllerHandlerMapping;
import org.broadleafcommerce.openadmin.web.controller.config.AdminWebMvcConfiguration;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * This class is designed to be the default handler mapping for {@link Controller}/{@link RequestMapping} annotations in
 * the admin so long as the class doesn't have {@link AdminBasicEntityController} as an ancestor.
 * <p>
 * This class is set as the default handler mapping in {@link AdminWebMvcConfiguration}.
 * <p>
 * The reason these mappings are separated out is that admin pipeline controllers (ones that extend from {@link
 * AdminBasicEntityController}) have some catch all mappings that will not allow mappings located in {@link
 * FrameworkControllerHandlerMapping} to be found if the admin pipeline controller is located in the default handler
 * mapping.
 * <p>
 * The admin handler mappings in play in order of precedence from highest to lowest are:
 * <ol>
 * <li>{@link AdminRequestMappingHandlerMapping}</li>
 * <li>{@link FrameworkControllerHandlerMapping}</li>
 * <li>{@link AdminControllerHandlerMapping}</li>
 * </ol>
 *
 * @author Philip Baggett (pbaggett)
 * @see AdminWebMvcConfiguration
 * @see AdminControllerHandlerMapping
 */
public class AdminRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return (AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) ||
                AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class))
                && !AdminBasicEntityController.class.isAssignableFrom(beanType);
    }
}
