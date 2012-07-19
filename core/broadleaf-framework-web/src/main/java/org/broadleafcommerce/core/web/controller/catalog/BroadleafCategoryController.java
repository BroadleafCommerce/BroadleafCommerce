package org.broadleafcommerce.core.web.controller.catalog;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.search.domain.ProductSearchResult;
import org.broadleafcommerce.core.search.service.ProductSearchService;
import org.broadleafcommerce.core.web.catalog.CategoryHandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    
	@Resource(name = "blProductSearchService")
	protected ProductSearchService productSearchService;

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		Category category = (Category) request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME);
		assert(category != null);
		
		model.addObject(CATEGORY_ATTRIBUTE_NAME, category);
		
		//TODO: Introduce paging, filtering, sorting
		ProductSearchResult result = productSearchService.findProductsByCategory(category, null);
    	model.addObject(PRODUCTS_ATTRIBUTE_NAME, result.getProducts());

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
