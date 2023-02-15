/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.controller.catalog;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.common.util.UrlUtil;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchResult;
import org.broadleafcommerce.core.search.redirect.domain.SearchRedirect;
import org.broadleafcommerce.core.search.redirect.service.SearchRedirectService;
import org.broadleafcommerce.core.search.service.SearchService;
import org.broadleafcommerce.core.web.service.SearchFacetDTOService;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles searching the catalog for a given search term. Will apply product search criteria
 * such as filters, sorts, and pagination if applicable
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class BroadleafSearchController extends AbstractCatalogController {

    @Resource(name = "blSearchService")
    protected SearchService searchService;

    @Resource(name = "blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;
    
    @Resource(name = "blSearchFacetDTOService")
    protected SearchFacetDTOService facetService;
    @Resource(name = "blSearchRedirectService")
    protected SearchRedirectService searchRedirectService;
    protected static String searchView = "catalog/search";
    
    protected static String PRODUCTS_ATTRIBUTE_NAME = "products";
    protected static String FACETS_ATTRIBUTE_NAME = "facets";  
    protected static String PRODUCT_SEARCH_RESULT_ATTRIBUTE_NAME = "result";  
    protected static String ACTIVE_FACETS_ATTRIBUTE_NAME = "activeFacets";  
    protected static String ORIGINAL_QUERY_ATTRIBUTE_NAME = "originalQuery";  
    protected static String ALL_PRODUCTS_ATTRIBUTE_NAME = "blcAllDisplayedProducts";
    protected static String ALL_SKUS_ATTRIBUTE_NAME = "blcAllDisplayedSkus";

    public String search(Model model, HttpServletRequest request, HttpServletResponse response,String query) throws ServletException, IOException, ServiceException {

        if (request.getParameterMap().containsKey("facetField")) {
            // If we receive a facetField parameter, we need to convert the field to the 
            // product search criteria expected format. This is used in multi-facet selection. We 
            // will send a redirect to the appropriate URL to maintain canonical URLs
            
            String fieldName = request.getParameter("facetField");
            List<String> activeFieldFilters = new ArrayList<String>();
            Map<String, String[]> parameters = new HashMap<String, String[]>(request.getParameterMap());
            
            for (Iterator<Entry<String,String[]>> iter = parameters.entrySet().iterator(); iter.hasNext();){
                Map.Entry<String, String[]> entry = iter.next();
                String key = entry.getKey();
                if (key.startsWith(fieldName + "-")) {
                    activeFieldFilters.add(key.substring(key.indexOf('-') + 1));
                    iter.remove();
                }
            }
            
            parameters.remove(SearchCriteria.PAGE_NUMBER);
            parameters.put(fieldName, activeFieldFilters.toArray(new String[activeFieldFilters.size()]));
            parameters.remove("facetField");
            
            String newUrl = ProcessorUtils.getUrl(request.getRequestURL().toString(), parameters);
            return "redirect:" + newUrl;
        } else {
            // Else, if we received a GET to the category URL (either the user performed a search or we redirected
            // from the POST method, we can actually process the results
            SearchRedirect handler = searchRedirectService.findSearchRedirectBySearchTerm(query);
                   
            if (handler != null) {
                String contextPath = request.getContextPath();
                String url = UrlUtil.fixRedirectUrl(contextPath, handler.getUrl());
                response.sendRedirect(url);
                return null;
            }

            if (StringUtils.isNotEmpty(query)) {
                SearchCriteria searchCriteria = facetService.buildSearchCriteria(request);

                if (StringUtils.isEmpty(searchCriteria.getQuery())) {
                    // if our query is empty or null, we want to redirect.
                    return "redirect:/";
                }

                SearchResult result = getSearchService().findSearchResults(searchCriteria);
                
                facetService.setActiveFacetResults(result.getFacets(), request);
                
                model.addAttribute(PRODUCTS_ATTRIBUTE_NAME, result.getProducts());
                model.addAttribute(FACETS_ATTRIBUTE_NAME, result.getFacets());
                model.addAttribute(PRODUCT_SEARCH_RESULT_ATTRIBUTE_NAME, result);
                model.addAttribute(ORIGINAL_QUERY_ATTRIBUTE_NAME, query);
                if (result.getProducts() != null) {
                    model.addAttribute(ALL_PRODUCTS_ATTRIBUTE_NAME, new HashSet<Product>(result.getProducts()));
                }

            }
            
        }

        updateQueryRequestAttribute(query);

        return getSearchView();
    }

    public String getSearchView() {
        return searchView;
    }

    protected void updateQueryRequestAttribute(String query) {
        if (StringUtils.isNotEmpty(query)) {
            BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
            if (brc != null && brc.getAdditionalProperties() != null) {
                brc.getAdditionalProperties().put("blcSearchKeyword", query);
            }
        }
    }

    protected SearchService getSearchService() {
        return searchService;
    }

}

