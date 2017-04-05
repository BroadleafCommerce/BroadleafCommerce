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

import org.apache.commons.lang3.ArrayUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.web.controller.catalog.BroadleafCategoryController;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafAttributeModifierProcessor;
import org.broadleafcommerce.presentation.model.BroadleafAttributeModifier;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * A Thymeleaf processor that generates a search query href that will reflect the current 
 * search criteria in addition to the requested sort string
 * 
 * <p>
 * This is intended to be used in an anchor tag:
 * 
 * <pre>
 * {@code
 *  <a blc:addsortlink="price">Sort By Price</a>
 * }
 * </pre>
 * 
 * <p>
 * Produces:
 * 
 * <pre>
 * {@code
 *  <a class="asc" href="http://mysite.com/category?sort=price+asc">Sort By Price</a>
 * }
 * </pre>
 * 
 * <p>
 * This sort link can then be picked up by the {@link BroadleafCategoryController} to actually translate search queries based
 * on that query parameter. If there is no sort active on the request then this will print out a link to sort ascending.
 * Otherwise the link will output the non-active sort (so that you can switch between them).
 * 
 * @author apazzolini
 */
@Component("blAddSortLinkProcessor")
@ConditionalOnTemplating
public class AddSortLinkProcessor extends AbstractBroadleafAttributeModifierProcessor {

    protected boolean allowMultipleSorts = false;

    @Override
    public String getName() {
        return "addsortlink";
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

        String key = SearchCriteria.SORT_STRING;

        List<String[]> sortedFields = new ArrayList<>();

        String[] paramValues = params.get(key);
        if (ArrayUtils.isNotEmpty(paramValues)) {
            String sortQueries = paramValues[0];
            for (String sortQuery : sortQueries.split(",")) {
                String[] sort = sortQuery.split(" ");
                if (sort.length == 2) {
                    sortedFields.add(new String[] { sort[0], sort[1] });
                }
            }
        }

        boolean currentlySortingOnThisField = false;
        boolean currentlyAscendingOnThisField = false;

        for (String[] sortedField : sortedFields) {
            if (attributeValue.equals(sortedField[0])) {
                currentlySortingOnThisField = true;
                currentlyAscendingOnThisField = sortedField[1].equals("asc");
                sortedField[1] = currentlyAscendingOnThisField ? "desc" : "asc";
            }
        }

        String sortString = attributeValue;
        String classString = "";

        if (currentlySortingOnThisField) {
            classString += "active ";
            if (currentlyAscendingOnThisField) {
                sortString += " desc";
                classString += "asc ";
            } else {
                sortString += " asc";
                classString += "desc ";
            }
        } else {
            sortString += " asc";
            classString += "asc ";
            params.remove(SearchCriteria.PAGE_NUMBER);
        }

        if (allowMultipleSorts) {
            StringBuilder sortSb = new StringBuilder();
            for (String[] sortedField : sortedFields) {
                sortSb.append(sortedField[0]).append(" ").append(sortedField[1]).append(",");
            }

            sortString = sortSb.toString();
            if (sortString.charAt(sortString.length() - 1) == ',') {
                sortString = sortString.substring(0, sortString.length() - 1);
            }
        }

        params.put(key, new String[] { sortString });

        String url = ProcessorUtils.getUrl(baseUrl, params);
        Map<String, String> newAttributes = new HashMap<>();
        newAttributes.put("class", classString);
        newAttributes.put("href", url);
        return new BroadleafAttributeModifier(newAttributes);
    }
}
