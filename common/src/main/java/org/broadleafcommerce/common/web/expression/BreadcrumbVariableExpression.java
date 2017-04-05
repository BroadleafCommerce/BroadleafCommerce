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
/**
 * 
 */
package org.broadleafcommerce.common.web.expression;

import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbService;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blBreadcrumbVariableExpression")
@ConditionalOnTemplating
public class BreadcrumbVariableExpression implements BroadleafVariableExpression {

    @Resource(name = "blBreadcrumbService")
    protected BreadcrumbService breadcrumbService;
    
    @Override
    public String getName() {
        return "breadcrumbs";
    }

    public List<BreadcrumbDTO> getBreadcrumbs() {
        String baseUrl = getBaseUrl();
        Map<String, String[]> params = getParams();
        return breadcrumbService.buildBreadcrumbDTOs(baseUrl, params);
    }

    protected String getBaseUrl() {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        
        if (brc != null) {
            return brc.getRequest().getRequestURI();
        }
        return "";
    }
    
    protected Map<String, String[]> getParams() {
        Map<String, String[]> paramMap = new HashMap<>();
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        
        if (brc != null) {
            paramMap = BroadleafRequestContext.getRequestParameterMap();
            if (paramMap != null) {
                paramMap = new HashMap<>(paramMap);
            }
        }
        return paramMap;
    }
}
