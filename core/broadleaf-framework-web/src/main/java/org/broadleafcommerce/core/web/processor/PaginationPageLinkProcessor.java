/*
 * #%L
 * BroadleafCommerce Framework Web
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

package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafAttributeModifierProcessor;
import org.broadleafcommerce.presentation.model.BroadleafAttributeModifier;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * A Thymeleaf processor that processes the value attribute on the element it's tied to
 * with a predetermined value based on the SearchFacetResultDTO object that is passed into this
 * processor.
 * 
 * 
 * @author apazzolini
 */
@Component("blPaginationPageLinkProcessor")
@ConditionalOnTemplating
public class PaginationPageLinkProcessor extends AbstractBroadleafAttributeModifierProcessor {

    @Override
    public String getName() {
        return "paginationpagelink";
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    public BroadleafAttributeModifier getModifiedAttributes(String tagName, Map<String, String> tagAttributes, String attributeName, String attributeValue, BroadleafTemplateContext context) {
        BroadleafRequestContext blcContext = BroadleafRequestContext.getBroadleafRequestContext();
        HttpServletRequest request = blcContext.getRequest();

        String baseUrl = request.getRequestURL().toString();
        Map<String, String[]> params = new HashMap<>(request.getParameterMap());

        Integer page = (Integer) context.parseExpression(attributeValue);
        if (page != null && page > 1) {
            params.put(SearchCriteria.PAGE_NUMBER, new String[] { page.toString() });
        } else {
            params.remove(SearchCriteria.PAGE_NUMBER);
        }

        String url = ProcessorUtils.getUrl(baseUrl, params);
        Map<String, String> newAttributes = new HashMap<>();
        newAttributes.put("href", url);
        return new BroadleafAttributeModifier(newAttributes);
    }

}
