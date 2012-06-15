package org.broadleafcommerce.core.web.controller.catalog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.web.catalog.ProductHandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * This class works in combination with the CategoryHandlerMapping which finds a category based upon
 * the passed in URL.
 *
 * @author bpolster
 */
public class ProductController implements Controller {
	
    private String defaultProductTemplateName="product";
    private static String MODEL_ATTRIBUTE_NAME="product";    

	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView();
		Product product = (Product) request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME);
		assert(product != null);
		
		model.addObject(MODEL_ATTRIBUTE_NAME, product);

		if (product.getDisplayTemplate() != null && !("".equals(product.getDisplayTemplate()))) {
			model.setViewName(product.getDisplayTemplate());	
		} else {
			model.setViewName(getDefaultProductTemplateName());
		}
		return model;
	}

	public String getDefaultProductTemplateName() {
		return defaultProductTemplateName;
	}

	public void setDefaultProductTemplateName(String defaultProductTemplateName) {
		this.defaultProductTemplateName = defaultProductTemplateName;
	}
}
