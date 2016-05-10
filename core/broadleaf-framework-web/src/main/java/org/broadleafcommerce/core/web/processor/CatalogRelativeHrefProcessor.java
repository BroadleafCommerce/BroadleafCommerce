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
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressions;

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
public class CatalogRelativeHrefProcessor extends AbstractAttributeModifierAttrProcessor {

    private static final String RHREF = "rhref";
    private static final String HREF = "href";

    @Resource(name = "blCatalogURLService")
    protected CatalogURLService catalogURLService;

    public CatalogRelativeHrefProcessor() {
        super(RHREF);
    }

    @Override
    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
        Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
                .parseExpression(arguments.getConfiguration(), arguments, element.getAttributeValue(attributeName));
        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();

        String relativeHref = buildRelativeHref(expression, arguments, request);
               
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put(HREF, relativeHref);
        return attrs;
    }

    protected String buildRelativeHref(Expression expression, Arguments arguments, HttpServletRequest request) {
        Object result = expression.execute(arguments.getConfiguration(), arguments);
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
    protected ModificationType getModificationType(Arguments arguments, Element element, String attributeName, String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }

    @Override
    protected boolean removeAttributeIfEmpty(Arguments arguments, Element element, String attributeName, String newAttributeName) {
        return true;
    }

    @Override
    protected boolean recomputeProcessorsAfterExecution(Arguments arguments, Element element, String attributeName) {
        return false;
    }

    @Override
    public int getPrecedence() {
        return 0;
    }
}
