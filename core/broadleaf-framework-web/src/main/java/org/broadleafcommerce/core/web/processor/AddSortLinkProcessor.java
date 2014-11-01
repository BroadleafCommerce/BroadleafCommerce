/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.broadleafcommerce.core.web.controller.catalog.BroadleafCategoryController;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;

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
public class AddSortLinkProcessor extends AbstractAttributeModifierAttrProcessor {
    
    protected boolean allowMultipleSorts = false;
    
    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public AddSortLinkProcessor() {
        super("addsortlink");
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
        Map<String, String> attrs = new HashMap<String, String>();
        
        BroadleafRequestContext blcContext = BroadleafRequestContext.getBroadleafRequestContext();
        HttpServletRequest request = blcContext.getRequest();
        
        String baseUrl = request.getRequestURL().toString();
        Map<String, String[]> params = new HashMap<String, String[]>(request.getParameterMap());
        
        String key = ProductSearchCriteria.SORT_STRING;
        String sortField = element.getAttributeValue(attributeName);
        
        List<String[]> sortedFields = new ArrayList<String[]>();
        
        String[] paramValues = params.get(key);
        if (paramValues != null && paramValues.length > 0) {
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
            if (sortField.equals(sortedField[0])) {
                currentlySortingOnThisField = true;
                currentlyAscendingOnThisField = sortedField[1].equals("asc");
                sortedField[1] = currentlyAscendingOnThisField ? "desc" : "asc";
            }
        }
        
        String sortString = sortField;
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
            params.remove(ProductSearchCriteria.PAGE_NUMBER);
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
        
        params.put(key, new String[] { sortString } );
        
        String url = ProcessorUtils.getUrl(baseUrl, params);
        
        attrs.put("class", classString);
        attrs.put("href", url);
        return attrs;
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
