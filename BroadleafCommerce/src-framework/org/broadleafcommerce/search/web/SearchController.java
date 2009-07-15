package org.broadleafcommerce.search.web;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
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
            @RequestParam(required = false) Long[] categoryId,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) Boolean ajax) throws CorruptIndexException, IOException, ParseException {

        SearchQuery input = new SearchQuery();
        input.setQueryString(queryString);
        List<Sku> skus = null;

        skus = searchService.performSearch(input.getQueryString());

        if(queryString.equals(originalQueryString)) {
            if (categoryId != null) {
                for (Iterator<Sku> itr = skus.iterator(); itr.hasNext();) {
                    Sku sku = itr.next();
                    List<Product> parents = sku.getAllParentProducts();
                    boolean found = false;
                    for(Product parent : parents) {
                        if (ArrayUtils.contains(categoryId, parent.getDefaultCategory().getId())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        itr.remove();
                    }
                }
            }
            if (minPrice != null && maxPrice != null) {
                Money minimumPrice = new Money(minPrice.replaceAll("[^0-9.]", ""));
                Money maximumPrice = new Money(maxPrice.replaceAll("[^0-9.]", ""));
                for (Iterator<Sku> itr = skus.iterator(); itr.hasNext();) {
                    Sku sku = itr.next();
                    if (sku.getSalePrice().lessThan(minimumPrice) || sku.getSalePrice().greaterThan(maximumPrice)) {
                        itr.remove();
                    }
                }
            }
        }

        model.addAttribute("queryString", input.getQueryString());
        model.addAttribute("skus", skus);

        if (ajax == null || !ajax.booleanValue()) {
            return "search";
        } else {
            return "searchAjax";
        }
    }
}
