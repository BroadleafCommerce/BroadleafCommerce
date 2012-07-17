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

package org.broadleafcommerce.core.web.controller.catalog;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.domain.SearchIntercept;
import org.broadleafcommerce.core.search.domain.SearchQuery;
import org.broadleafcommerce.core.search.service.SearchService;
import org.broadleafcommerce.core.web.search.SearchFilterUtil;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles searching the catalog for a given search term.
 */
public class BroadleafSearchController extends AbstractCatalogController {

	@Resource(name = "blSearchService")
    protected SearchService searchService;
	
	protected String searchView = "ajax:catalog/search";

	//TODO: This isn't really implemented
    @SuppressWarnings("unchecked")
	public String search(Model model, HttpServletRequest request,
            String queryString,
            String originalQueryString) {
        SearchQuery input = new SearchQuery(queryString);
        
        SearchIntercept intercept = searchService.getInterceptForTerm(queryString);
        if (intercept != null) {
            return "redirect:"+ intercept.getRedirect();
        }
        
        List<Product> products = null;

        products = searchService.performSearch(input.getQueryString());
        SearchFilterUtil.filterProducts(products, request.getParameterMap(), new String[]{"manufacturer","defaultCategory.id","sku.salePrice"});

        model.addAttribute("queryString", input.getQueryString());
        model.addAttribute("products", products);

        // Separate results by category
        List<Category> categories = new ArrayList<Category>();
        Map<Long, List<Product> > categoryGroups= new HashMap<Long, List<Product> >();
        for (Product product : products) {
            Category cat = product.getDefaultCategory();
            if (!categoryGroups.containsKey(cat.getId())) {
                categories.add(cat);
                categoryGroups.put(cat.getId(), new ArrayList<Product>());
            }
            categoryGroups.get(cat.getId()).add(product);
        }
        model.addAttribute("categories", categories);
        model.addAttribute("categoryGroups", categoryGroups);
        
        return getSearchView();
    }

	public String getSearchView() {
		return searchView;
	}

	public void setSearchView(String searchView) {
		this.searchView = searchView;
	}
    
}
