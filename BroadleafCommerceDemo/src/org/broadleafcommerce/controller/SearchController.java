package org.broadleafcommerce.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.search.domain.SearchQuery;
import org.broadleafcommerce.search.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

@Controller
public class SearchController {
	@Resource
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
	
	@RequestMapping(method = {RequestMethod.POST})
	public String search (ModelMap model, HttpServletRequest request, @RequestParam(required = true) String queryString) throws CorruptIndexException, IOException, ParseException {

		SearchQuery input = new SearchQuery();
		input.setQueryString(queryString);
		List<Sku> skus = null;
		
		skus = searchService.performSearch(input.getQueryString());
		
        model.addAttribute("skus", skus);

		return "search";
	}
}
