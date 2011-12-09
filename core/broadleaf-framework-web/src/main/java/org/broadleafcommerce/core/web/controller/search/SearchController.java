/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.controller.search;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.domain.SearchIntercept;
import org.broadleafcommerce.core.search.domain.SearchQuery;
import org.broadleafcommerce.core.search.service.SearchService;
import org.broadleafcommerce.core.web.search.SearchFilterUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Resource(name="blSearchService")
    private SearchService searchService;

    @RequestMapping(method = {RequestMethod.GET})
    public String blank (ModelMap model, HttpServletRequest request)
    {
        return "search";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String search (ModelMap model,
            HttpServletRequest request,
            @RequestParam(required = true) String queryString,
            @RequestParam(required = false) String originalQueryString,
            @RequestParam(required = false) Boolean ajax) {

        SearchQuery input = new SearchQuery();
        input.setQueryString(queryString);
        SearchIntercept intercept = searchService.getInterceptForTerm(queryString);
        if (intercept != null) {
            return "redirect:"+ intercept.getRedirect();
        }
        List<Product> products = null;

        products = searchService.performSearch(input.getQueryString());
        SearchFilterUtil.filterProducts(products, request.getParameterMap(), new String[]{"manufacturer","defaultCategory.id","sku.salePrice"});

        model.addAttribute("queryString", input.getQueryString());
        model.addAttribute("products", products);

        // separate results by category
        List<Category> categories = new ArrayList<Category>();
        Map<Long, List<Product> > categoryGroups= new HashMap<Long, List<Product> >();
        for (Product prod : products) {
            Category cat = prod.getDefaultCategory();
            if (!categoryGroups.containsKey(cat.getId())) {
                categories.add(cat);
                categoryGroups.put(cat.getId(), new ArrayList<Product>());
            }
            categoryGroups.get(cat.getId()).add(prod);
        }
        model.addAttribute("categories", categories);
        model.addAttribute("categoryGroups", categoryGroups);

        if (ajax == null || !ajax.booleanValue() || (originalQueryString != null && !originalQueryString.equals(queryString))) {
            return "search";
        } else {
            return "searchAjax";
        }
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
}
