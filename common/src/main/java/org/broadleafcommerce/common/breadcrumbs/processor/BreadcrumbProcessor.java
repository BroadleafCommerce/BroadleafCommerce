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

package org.broadleafcommerce.common.breadcrumbs.processor;

import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbService;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.dialect.AbstractBroadleafModelVariableModifierProcessor;
import org.broadleafcommerce.common.web.domain.BroadleafTemplateContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * A Thymeleaf processor that will add a list of BreadcrumbDTOs to the model.
 *
 * @author bpolster
 */
@Component("blBreadcrumbProcessor")
public class BreadcrumbProcessor extends AbstractBroadleafModelVariableModifierProcessor {

    @Resource(name = "blBreadcrumbService")
    protected BreadcrumbService breadcrumbService;

    @Override
    public String getName() {
        return "breadcrumbs";
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    public void populateModelVariables(String tagName, Map<String, String> tagAttributes, Map<String, Object> newModelVars, BroadleafTemplateContext context) {
        String baseUrl = getBaseUrl(tagAttributes);
        Map<String, String[]> params = getParams(tagAttributes);
        List<BreadcrumbDTO> dtos = breadcrumbService.buildBreadcrumbDTOs(baseUrl, params);
        String resultVar = tagAttributes.get("resultVar");

        if (resultVar == null) {
            resultVar = "breadcrumbs";
        }

        if (!CollectionUtils.isEmpty(dtos)) {
            newModelVars.put(resultVar, dtos);
        }

    }

    protected String getBaseUrl(Map<String, String> tagAttributes) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        
        if (brc != null) {
            return brc.getRequest().getRequestURI();
        }
        return "";
    }
    
    protected Map<String, String[]> getParams(Map<String, String> tagAttributes) {
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        
        if (brc != null) {
            paramMap = BroadleafRequestContext.getRequestParameterMap();
            if (paramMap != null) {
                paramMap = new HashMap<String, String[]>(paramMap);
            }
        }
        return paramMap;
    }
    
}
