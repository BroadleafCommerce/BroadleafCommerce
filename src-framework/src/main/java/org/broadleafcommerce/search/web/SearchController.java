package org.broadleafcommerce.search.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.search.domain.SearchQuery;
import org.broadleafcommerce.search.service.SearchService;
import org.broadleafcommerce.search.util.SearchFilterUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @Resource(name="blSearchService")
    private SearchService searchService;

    public SearchService getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

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
        List<Product> products = null;

        products = searchService.performSearch(input.getQueryString());
        SearchFilterUtil.filterProducts(products, request.getParameterMap(), new String[]{"manufacturer","defaultCategory.id","skus[0].salePrice"});

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
}
