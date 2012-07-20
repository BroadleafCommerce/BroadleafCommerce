package org.broadleafcommerce.core.web.controller.catalog;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.broadleafcommerce.core.search.domain.ProductSearchResult;
import org.broadleafcommerce.core.search.service.ProductSearchService;
import org.broadleafcommerce.core.web.catalog.CategoryHandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Iterator;
import java.util.Map;

/**
 * This class works in combination with the CategoryHandlerMapping which finds a category based upon
 * the passed in URL.
 *
 * @author bpolster
 */
public class BroadleafCategoryController extends BroadleafAbstractController implements Controller {
	
    protected String defaultCategoryView = "catalog/category";
    protected static String CATEGORY_ATTRIBUTE_NAME = "category";  
    protected static String PRODUCTS_ATTRIBUTE_NAME = "products";  
    protected static String FACETS_ATTRIBUTE_NAME = "facets";  
    
	@Resource(name = "blProductSearchService")
	protected ProductSearchService productSearchService;

	@Override
	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		Category category = (Category) request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME);
		assert(category != null);
		
		ProductSearchCriteria searchCriteria = new ProductSearchCriteria();
		
		Map<String, String[]> params = request.getParameterMap();
		for (Iterator<Map.Entry<String,String[]>> iter = params.entrySet().iterator(); iter.hasNext();){
			Map.Entry<String, String[]> entry = iter.next();
			String key = entry.getKey();
			
			if (key.equals(ProductSearchCriteria.SORT_STRING)) {
				searchCriteria.setSortQuery(entry.getValue()[0]);
				iter.remove();
			}
			
			if (key.equals(ProductSearchCriteria.PAGE_NUMBER)) {
				searchCriteria.setPage(Integer.parseInt(entry.getValue()[0]));
				iter.remove();
			}
			
			if (key.equals(ProductSearchCriteria.PAGE_SIZE_STRING)) {
				searchCriteria.setPageSize(Integer.parseInt(entry.getValue()[0]));
				iter.remove();
			}
		}
		
		searchCriteria.setFilterCriteria(params);
		
		model.addObject(CATEGORY_ATTRIBUTE_NAME, category);
		
		//TODO: Introduce paging, filtering, sorting
		ProductSearchResult result = productSearchService.findProductsByCategory(category, searchCriteria);
    	model.addObject(PRODUCTS_ATTRIBUTE_NAME, result.getProducts());
    	model.addObject(FACETS_ATTRIBUTE_NAME, result.getFacets());

		if (StringUtils.isNotEmpty(category.getDisplayTemplate())) {
			model.setViewName(category.getDisplayTemplate());	
		} else {
			model.setViewName(getDefaultCategoryView());
		}
		
		return model;
	}

	public String getDefaultCategoryView() {
		return defaultCategoryView;
	}

	public void setDefaultCategoryView(String defaultCategoryView) {
		this.defaultCategoryView = defaultCategoryView;
	}
	
}
