package org.broadleafcommerce.core.web.controller.catalog;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

public class DefaultCategoryBrowseController extends AbstractCatalogController {
	
	//FIXME-APA: This needs to be dynamic
	public String hotSauces(HttpServletRequest request, HttpServletResponse response, Model model) {
		Category category = catalogService.findCategoriesByName("Hot Sauces").get(0);
		List<Product> products = catalogService.findProductsForCategory(category);
		
		model.addAttribute("products", products);
		
		return "category";
	}
	
	//FIXME-APA: This needs to be dynamic
	public String productDetail(HttpServletRequest request, HttpServletResponse response, Model model,
			Long productId) {
		Product product = catalogService.findProductById(productId);
		
		model.addAttribute("product", product);
		return "product";
	}
	
}
