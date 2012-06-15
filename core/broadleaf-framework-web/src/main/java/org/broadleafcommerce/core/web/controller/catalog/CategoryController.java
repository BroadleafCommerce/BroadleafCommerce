package org.broadleafcommerce.core.web.controller.catalog;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.web.catalog.CategoryHandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * This class works in combination with the CategoryHandlerMapping which finds a category based upon
 * the passed in URL.
 *
 * @author bpolster
 */
public class CategoryController implements Controller {
	
    private String defaultCategoryTemplateName="category";
    private static String CATEGORY_ATTRIBUTE_NAME="category";  
    private static String PRODUCTS_ATTRIBUTE_NAME="products";  
    
	@Resource(name = "blCatalogService")
	protected CatalogService catalogService;

	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView();
		Category category = (Category) request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME);
		assert(category != null);
		
		model.addObject(CATEGORY_ATTRIBUTE_NAME, category);
		
		//TODO: Introduce paging, filtering, sorting
		List<Product> productList = catalogService.findActiveProductsByCategory(category, SystemTime.asDate());
    	model.addObject(PRODUCTS_ATTRIBUTE_NAME, productList);

		if (category.getDisplayTemplate() != null && !("".equals(category.getDisplayTemplate()))) {
			model.setViewName(category.getDisplayTemplate());	
		} else {
			model.setViewName(getDefaultCategoryTemplateName());
		}
		return model;
	}

	public String getDefaultCategoryTemplateName() {
		return defaultCategoryTemplateName;
	}

	public void setDefaultCategoryTemplateName(String defaultCategoryTemplateName) {
		this.defaultCategoryTemplateName = defaultCategoryTemplateName;
	}

}
