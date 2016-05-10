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
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * A Thymeleaf processor that will add a list of BreadcrumbDTOs to the model.
 *
 * @author bpolster
 */
public class BreadcrumbProcessor extends AbstractModelVariableModifierProcessor {

    @Resource(name = "blBreadcrumbService")
    protected BreadcrumbService breadcrumbService;

    /**
     * Sets the name of this processor to be used in the Thymeleaf template
     */
    public BreadcrumbProcessor() {
        super("breadcrumbs");
    }

    @Override
    public int getPrecedence() {
        return 1000;
    }

    @Override
    protected void modifyModelAttributes(Arguments arguments, Element element) {
        String baseUrl = getBaseUrl(arguments, element);
        Map<String, String[]> params = getParams(arguments, element);
        List<BreadcrumbDTO> dtos = breadcrumbService.buildBreadcrumbDTOs(baseUrl, params);
        String resultVar = element.getAttributeValue("resultVar");
        
        if (resultVar == null) {
            resultVar = "breadcrumbs";
        }
        
        if (!CollectionUtils.isEmpty(dtos)) {
            addToModel(arguments, resultVar, dtos);
        }
    }

    protected String getBaseUrl(Arguments arguments, Element element) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();

        if (brc != null) {
            return brc.getRequest().getRequestURI();
        }
        return "";
    }

    protected Map<String, String[]> getParams(Arguments arguments, Element element) {
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
