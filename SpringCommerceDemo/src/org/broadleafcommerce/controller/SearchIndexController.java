package org.broadleafcommerce.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.search.service.SearchService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class SearchIndexController extends SimpleFormController {
	private SearchService searchService;

	public SearchService getSearchService() {
		return searchService;
	}

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		return new Object();
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {

		System.out.println("------------------------ Creating Index;");
		searchService.rebuildSellableItemIndex();
		System.out.println("------------------------ Finished Creating Index;");

		return super.onSubmit(request, response, command, errors);
	}
}
