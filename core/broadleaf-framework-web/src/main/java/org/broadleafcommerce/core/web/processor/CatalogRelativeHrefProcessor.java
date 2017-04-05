/*
 * #%L
 * BroadleafCommerce CMS Module
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
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogURLService;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafAttributeModifierProcessor;
import org.broadleafcommerce.presentation.model.BroadleafAttributeModifier;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * For use with category and product entities.   Creates a relative URL using the
 * current URI appended with the url-key (or last fragment of the url).
 * 
 * Takes in a category or product object as a parameter.
 * 
 * Uses the current request for the baseURI.
 * 
 * This implementation will also a categoryId or productId to the end of the URL it generates.
 * 
 * @author bpolster
 */
@Component("blCatalogRelativeHrefProcessor")
@ConditionalOnTemplating
public class CatalogRelativeHrefProcessor extends AbstractBroadleafAttributeModifierProcessor {

    private static final String RHREF = "rhref";
    private static final String HREF = "href";

    @Resource(name = "blCatalogURLService")
    protected CatalogURLService catalogURLService;

    @Override
    public String getName() {
        return "RHREF";
    }
    
    @Override
    public int getPrecedence() {
        return 0;
    }

    protected String buildRelativeHref(String tagName, Map<String, String> tagAttributes, String attributeName, String attributeValue, BroadleafTemplateContext context) {
        Object result = context.parseExpression(attributeValue);
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();
        String currentUrl = request.getRequestURI();

        if (request.getQueryString() != null) {
            currentUrl = currentUrl + "?" + request.getQueryString();
        }

        if (result instanceof Product) {
            return catalogURLService.buildRelativeProductURL(currentUrl, (Product) result);
        } else if (result instanceof Category) {
            return catalogURLService.buildRelativeCategoryURL(currentUrl, (Category) result);
        }
        return "";
    }

    @Override
    public BroadleafAttributeModifier getModifiedAttributes(String tagName, Map<String, String> tagAttributes, String attributeName, String attributeValue, BroadleafTemplateContext context) {
        String relativeHref = buildRelativeHref(tagName, tagAttributes, attributeName, attributeValue, context);
        Map<String, String> newAttributes = new HashMap<>();
        newAttributes.put(HREF, relativeHref);
        return new BroadleafAttributeModifier(newAttributes);
    }

}
