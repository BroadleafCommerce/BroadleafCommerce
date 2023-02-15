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
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.template.TemplateOverrideExtensionManager;
import org.broadleafcommerce.common.template.TemplateType;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.TemplateTypeAware;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.common.web.deeplink.DeepLinkService;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchResult;
import org.broadleafcommerce.core.search.service.SearchService;
import org.broadleafcommerce.core.web.catalog.CategoryHandlerMapping;
import org.broadleafcommerce.core.web.service.SearchFacetDTOService;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class works in combination with the CategoryHandlerMapping which finds a category based upon
 * the passed in URL.
 *
 * @author bpolster
 */
public class BroadleafCategoryController extends BroadleafAbstractController implements Controller, TemplateTypeAware {
    
    protected static String defaultCategoryView = "catalog/category";
    protected static String CATEGORY_ATTRIBUTE_NAME = "category";  
    protected static String PRODUCTS_ATTRIBUTE_NAME = "products";  
    protected static String FACETS_ATTRIBUTE_NAME = "facets";  
    protected static String PRODUCT_SEARCH_RESULT_ATTRIBUTE_NAME = "result";  
    protected static String ACTIVE_FACETS_ATTRIBUTE_NAME = "activeFacets";  
    protected static String ALL_PRODUCTS_ATTRIBUTE_NAME = "blcAllDisplayedProducts";
    protected static String ALL_SKUS_ATTRIBUTE_NAME = "blcAllDisplayedSkus";
    protected static String ORIGINAL_QUERY_ATTRIBUTE_NAME = "originalQuery";

    @Resource(name = "blSearchService")
    protected SearchService searchService;
    
    @Resource(name = "blSearchFacetDTOService")
    protected SearchFacetDTOService facetService;
    
    @Autowired(required = false)
    @Qualifier("blCategoryDeepLinkService")
    protected DeepLinkService<Category> deepLinkService;

    @Resource(name = "blTemplateOverrideExtensionManager")
    protected TemplateOverrideExtensionManager templateOverrideManager;

    @Override
    @SuppressWarnings("unchecked")
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView model = new ModelAndView();
        
        if (request.getParameterMap().containsKey("facetField")) {
            // If we receive a facetField parameter, we need to convert the field to the 
            // product search criteria expected format. This is used in multi-facet selection. We 
            // will send a redirect to the appropriate URL to maintain canonical URLs
            
            String fieldName = request.getParameter("facetField");
            List<String> activeFieldFilters = new ArrayList<String>();
            Map<String, String[]> parameters = new HashMap<String, String[]>(request.getParameterMap());
            parameters.remove("blLocaleCode");
            
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
            model.setViewName("redirect:" + newUrl);
        } else {
            // Else, if we received a GET to the category URL (either the user clicked this link or we redirected
            // from the POST method, we can actually process the results
            
            Category category = (Category) request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME);
            assert(category != null);

            SearchCriteria searchCriteria = facetService.buildSearchCriteria(request);
            SearchResult result = getSearchService().findSearchResults(searchCriteria);

            facetService.setActiveFacetResults(result.getFacets(), request);
            
            model.addObject(CATEGORY_ATTRIBUTE_NAME, category);
            model.addObject(PRODUCTS_ATTRIBUTE_NAME, result.getProducts());
            model.addObject(FACETS_ATTRIBUTE_NAME, result.getFacets());
            model.addObject(PRODUCT_SEARCH_RESULT_ATTRIBUTE_NAME, result);
            if (request.getParameterMap().containsKey("q")) {
                model.addObject(ORIGINAL_QUERY_ATTRIBUTE_NAME, request.getParameter("q"));
            }
            model.addObject("BLC_PAGE_TYPE", "category");
            if (result.getProducts() != null) {
                model.addObject(ALL_PRODUCTS_ATTRIBUTE_NAME, new HashSet<Product>(result.getProducts()));
            }
            
            addDeepLink(model, deepLinkService, category);
            
            String templatePath = null;
            
            // Use the categories custom template if available
            if (StringUtils.isNotBlank(category.getDisplayTemplate())) {
                templatePath = category.getDisplayTemplate();
            } else {
                // Otherwise, use the controller default.
                templatePath = getDefaultCategoryView();
            }

            // Allow extension managers to override.
            ExtensionResultHolder<String> erh = new ExtensionResultHolder<String>();
            ExtensionResultStatusType extResult = templateOverrideManager.getProxy().getOverrideTemplate(erh, category);
            if (extResult != ExtensionResultStatusType.NOT_HANDLED) {
                templatePath = erh.getResult();
            }
            
            model.setViewName(templatePath);
        }
        return model;
    }

    public String getDefaultCategoryView() {
        return defaultCategoryView;
    }

    protected SearchService getSearchService() {
        return searchService;
    }

    @Override
    public String getExpectedTemplateName(HttpServletRequest request) {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context != null) {
            Category category = (Category) context.getRequest().getAttribute(CATEGORY_ATTRIBUTE_NAME);
            if (category != null && category.getDisplayTemplate() != null) {
                return category.getDisplayTemplate();
            }
        }
        return getDefaultCategoryView();
    }

    @Override
    public TemplateType getTemplateType(HttpServletRequest request) {
        return TemplateType.CATEGORY;
    }

}
