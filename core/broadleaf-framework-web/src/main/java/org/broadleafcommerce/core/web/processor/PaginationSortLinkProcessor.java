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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Thymeleaf Processor that replaces the "href" attribute on an <a/> element, maintaining the current search criteria
 * of the request and adding (or replacing, if it exists) the sort parameter on the request.
 *
 * @author Joseph Fridye (jfridye)
 */
public class PaginationSortLinkProcessor extends AbstractAttributeModifierAttrProcessor {

    public PaginationSortLinkProcessor() {
        super("pagination-sort-link");
    }

    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {

        Map<String, String> attributes = new HashMap<String, String>();

        HttpServletRequest request = BroadleafRequestContext.getBroadleafRequestContext().getRequest();

        String baseUrl = request.getRequestURL().toString();

        Map<String, String[]> params = new HashMap<String, String[]>(request.getParameterMap());

        String sort = element.getAttributeValue(attributeName);

        if (StringUtils.isNotBlank(sort)) {
            params.put(SearchCriteria.SORT_STRING, new String[]{sort});
        } else {
            params.remove(SearchCriteria.SORT_STRING);
        }

        // If there is a page number parameter, remove it. This ensures that when the search results refresh the
        // first page of results will be displayed.
        params.remove(SearchCriteria.PAGE_NUMBER);

        String url = ProcessorUtils.getUrl(baseUrl, params);

        attributes.put("href", url);

        return attributes;

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

}
