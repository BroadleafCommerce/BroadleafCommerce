package org.broadleafcommerce.core.web.controller.catalog;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.broadleafcommerce.core.search.domain.ProductSearchResult;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.service.ProductSearchService;
import org.broadleafcommerce.core.web.catalog.CategoryHandlerMapping;
import org.broadleafcommerce.core.web.service.SearchFacetDTOService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * This class works in combination with the CategoryHandlerMapping which finds a category based upon
 * the passed in URL.
 *
 * @author bpolster
 */
public class BroadleafCategoryController extends BroadleafAbstractController implements Controller {
	
    protected static String defaultCategoryView = "catalog/category";
    protected static String CATEGORY_ATTRIBUTE_NAME = "category";  
    protected static String PRODUCTS_ATTRIBUTE_NAME = "products";  
    protected static String FACETS_ATTRIBUTE_NAME = "facets";  
    protected static String ACTIVE_FACETS_ATTRIBUTE_NAME = "activeFacets";  
    
	@Resource(name = "blProductSearchService")
	protected ProductSearchService productSearchService;
	
	@Resource(name = "blSearchFacetDTOService")
	protected SearchFacetDTOService facetService;

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		
		Category category = (Category) request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME);
		assert(category != null);
		
		List<SearchFacetDTO> availableFacets = productSearchService.getCategoryFacets(category);
		ProductSearchCriteria searchCriteria = facetService.buildSearchCriteria(request, availableFacets);
		ProductSearchResult result = productSearchService.findProductsByCategory(category, searchCriteria);
		
		facetService.setActiveFacetResults(result.getFacets(), request);
    	
		model.addObject(CATEGORY_ATTRIBUTE_NAME, category);
    	model.addObject(PRODUCTS_ATTRIBUTE_NAME, result.getProducts());
    	model.addObject(FACETS_ATTRIBUTE_NAME, result.getFacets());

		if (StringUtils.isNotEmpty(category.getDisplayTemplate())) {
			model.setViewName(category.getDisplayTemplate());	
		} else {
			model.setViewName(getDefaultCategoryView());
		}
		
		return model;
	}

	public static String getDefaultCategoryView() {
		return defaultCategoryView;
	}

	public static void setDefaultCategoryView(String defaultCategoryView) {
		BroadleafCategoryController.defaultCategoryView = defaultCategoryView;
	}

}
