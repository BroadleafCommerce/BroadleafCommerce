package org.broadleafcommerce.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.broadleafcommerce.search.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

@Controller
public class SearchIndexController {
	@Resource
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
		System.out.println("------------------------ Index Creation Page;");
		return "searchIndex";
	}
	
	@RequestMapping(method = {RequestMethod.POST})
	public String build (ModelMap model, HttpServletRequest request)
	{
		System.out.println("------------------------ Creating Index;");
		try {
			searchService.rebuildSkuIndex();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("------------------------ Finished Creating Index;");

		return "searchIndex";
	}
}
