package org.broadleafcommerce.search.web;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.search.domain.SearchQuery;
import org.broadleafcommerce.search.service.SearchService;
import org.broadleafcommerce.util.money.Money;
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

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String search (ModelMap model,
            HttpServletRequest request,
            @RequestParam(required = true) String queryString,
            @RequestParam(required = false) String originalQueryString,
            @RequestParam(required = false, value="defaultCategory") Long[] categoryId,
            @RequestParam(required = false, value="min-money") String minPrice,
            @RequestParam(required = false, value="max-money") String maxPrice,
            @RequestParam(required = false) Boolean ajax) {

        SearchQuery input = new SearchQuery();
        input.setQueryString(queryString);
        List<Product> products = null;

        products = searchService.performSearch(input.getQueryString());

        if(queryString.equals(originalQueryString)) {
            if (categoryId != null) {
                for (Iterator<Product> itr = products.iterator(); itr.hasNext();) {
                    Product product = itr.next();
                    if (!ArrayUtils.contains(categoryId, product.getDefaultCategory().getId())) {
                        itr.remove();
                    }
                }
            }
            if (minPrice != null && maxPrice != null) {
                Money minimumPrice = new Money(minPrice.replaceAll("[^0-9.]", ""));
                Money maximumPrice = new Money(maxPrice.replaceAll("[^0-9.]", ""));
                for (Iterator<Product> itr = products.iterator(); itr.hasNext();) {
                    Product product = itr.next();
                    boolean found = false;
                    for (Iterator<Sku> skuItr = product.getSkus().iterator(); skuItr.hasNext();) {
                        Sku sku = skuItr.next();
                        if (sku.getSalePrice().lessThan(minimumPrice) || sku.getSalePrice().greaterThan(maximumPrice)) {
                            continue;
                        }
                        found = true;
                        break;
                    }
                    if (!found) {
                        itr.remove();
                    }
                }
            }
        }

        model.addAttribute("queryString", input.getQueryString());
        model.addAttribute("products", products);

        if (ajax == null || !ajax.booleanValue()) {
            return "search";
        } else {
            return "searchAjax";
        }
    }
}
