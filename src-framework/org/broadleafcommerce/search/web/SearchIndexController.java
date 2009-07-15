package org.broadleafcommerce.search.web;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.broadleafcommerce.search.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SearchIndexController {

    @Resource(name="blSearchService")
    private SearchService searchService;

    public SearchService getSearchService() {
        return searchService;
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    @RequestMapping(method = {RequestMethod.GET})
    public String index (ModelMap model, HttpServletRequest request)
    {
        return "searchIndex";
    }

    @RequestMapping(method = {RequestMethod.POST})
    public String build (ModelMap model, HttpServletRequest request) throws CorruptIndexException, LockObtainFailedException, IOException
    {
        searchService.rebuildProductIndex();

        return "searchIndex";
    }
}
