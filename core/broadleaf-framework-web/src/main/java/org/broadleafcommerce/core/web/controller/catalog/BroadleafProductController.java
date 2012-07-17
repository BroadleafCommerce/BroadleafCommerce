package org.broadleafcommerce.core.web.controller.catalog;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.web.catalog.ProductHandlerMapping;
import org.hibernate.tool.hbm2x.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class works in combination with the CategoryHandlerMapping which finds a category based upon
 * the passed in URL.
 *
 * @author bpolster
 */
public class BroadleafProductController extends BroadleafAbstractController implements Controller {
	
    protected String defaultProductView = "catalog/product";
    protected static String MODEL_ATTRIBUTE_NAME = "product";    

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		Product product = (Product) request.getAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME);
		assert(product != null);
		
		model.addObject(MODEL_ATTRIBUTE_NAME, product);

		if (StringUtils.isNotEmpty(product.getDisplayTemplate())) {
			model.setViewName(product.getDisplayTemplate());	
		} else {
			model.setViewName(getDefaultProductView());
		}
		return model;
	}

	public String getDefaultProductView() {
		return defaultProductView;
	}

	public void setDefaultProductView(String defaultProductView) {
		this.defaultProductView = defaultProductView;
	}
	
}