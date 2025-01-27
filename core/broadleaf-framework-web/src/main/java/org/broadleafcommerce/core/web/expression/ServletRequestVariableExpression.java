/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.expression;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service("blServletRequestVariableExpression")
@ConditionalOnTemplating
public class ServletRequestVariableExpression implements BroadleafVariableExpression {

    @Override
    public String getName() {
        return "httpServletRequest";
    }

    public String getRequestURI() {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        return context.getRequest() != null ? context.getRequest().getRequestURI() : null;
    }

    public String getParameter(final String parameter) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        return context.getRequest() != null ? context.getRequest().getParameter(parameter) : null;
    }

    public ServletUriComponentsBuilder uriBuilderFromCurrentRequest() {
        return ServletUriComponentsBuilder.fromCurrentRequest();
    }

}
