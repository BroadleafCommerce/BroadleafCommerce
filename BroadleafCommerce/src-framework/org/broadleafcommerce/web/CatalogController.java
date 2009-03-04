package org.broadleafcommerce.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

// TODO:  REQUIRED Support find by category id (double check product id logic)
public class CatalogController extends AbstractController {
	private static final int MAX_LEVELS = 10; // protection against recursive category structure
	protected final Log logger = LogFactory.getLog(getClass());
	private final UrlPathHelper pathHelper = new UrlPathHelper();
	private CatalogService catalogService;
	private String defaultCategoryView;
	private String defaultProductView;
	private Long rootCategoryId;
	private String categoryTemplatePrefix;

	@Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) {

    	HashMap<String,Object> model = new HashMap<String,Object>();
        boolean categoryError = false;

    	Category rootCategory = catalogService.findCategoryById(rootCategoryId);
    	if (rootCategory == null) {
    		throw new IllegalStateException("Catalog Controller configured incorrectly - root category not found." + rootCategoryId);
    	}

    	String path = pathHelper.getRequestUri(request).substring(pathHelper.getContextPath(request).length());
    	List<Category> categoryList = rootCategory.getChildCategoryURLMap().get(path);
    	if (categoryList != null) {
    		model.put("currentCategory", categoryList.get(categoryList.size()-1));
        	model.put("breadcrumbCategories", categoryList);
    	} else {
    		// TODO: nice to have would be nice to see if a lower level category is
    		// usable -- check to see if any of the parent categories are valid
    		categoryError = true;
    		model.put("currentCategory", rootCategory);
    		categoryList = new ArrayList<Category>();
    		categoryList.add(rootCategory);
    		model.put("breadcrumbCategories", categoryList);
    	}

        boolean productError = false;
        boolean productFound = false;

        String productId = request.getParameter("productId");
        if (productId != null) {
        	Product product = catalogService.findProductById(new Long(productId));
        	// TODO: REQUIRED - Validate that product exists in a valid category (e.g. to exclude christmas products)
        	if (product != null) {
        		model.put("currentProduct", product);
        		productFound = true;
        		if (categoryError) {
        			categoryError = buildBreadcrumbCategoriesFromProduct(product, categoryList);
        		}
        	} else {
        		productError = true;
        	}
        }

        Category currentCategory = categoryList.get(categoryList.size()-1);

       	model.put("categoryError", categoryError);
       	model.put("productError", productError);
		model.put("currentCategory", currentCategory);
    	model.put("breadcrumbCategories", categoryList);

        String view = defaultCategoryView;
    	if (productFound) {
    		// TODO: Nice to have: product logic similar to category below
    		view = defaultProductView;
    	} else {
    		if (currentCategory.getUrl() != null) {
    			view = currentCategory.getUrl();
    		} else if (currentCategory.getDisplayTemplate() != null) {
    			// TODO: Test this out
    			view = categoryTemplatePrefix + currentCategory.getDisplayTemplate();
    		} else {
    			view = defaultCategoryView;
    		}
    	}
        return new ModelAndView(view, model);
    }

	protected boolean buildBreadcrumbCategoriesFromProduct(Product product,
			List<Category> categoryList) {
		return buildBreadcrumbCategoriesFromCategory(product
				.getDefaultCategory(), categoryList);
	}

	protected boolean buildBreadcrumbCategoriesFromCategory(Category category,
			List<Category> categoryList) {
		categoryList.clear();
		int count = 0;
		Category tmpCategory = category;
		boolean categoryError = false;
		while (tmpCategory != null && count < MAX_LEVELS) {
			if (tmpCategory.isActive()) {
				categoryList.add(tmpCategory);
				tmpCategory = tmpCategory.getDefaultParentCategory();
			} else {
				categoryError = true;
			}
			count++;
		}

		Collections.reverse(categoryList);
		return categoryError;
	}

	public Long getRootCategoryId() {
		return rootCategoryId;
	}

	public void setRootCategoryId(Long rootCategoryId) {
		this.rootCategoryId = rootCategoryId;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public String getDefaultCategoryView() {
		return defaultCategoryView;
	}

	public void setDefaultCategoryView(String defaultCategoryView) {
		this.defaultCategoryView = defaultCategoryView;
	}

	public String getDefaultProductView() {
		return defaultProductView;
	}

	public void setDefaultProductView(String defaultProductView) {
		this.defaultProductView = defaultProductView;
	}

	public String getCategoryTemplatePrefix() {
		return categoryTemplatePrefix;
	}

	public void setCategoryTemplatePrefix(String categoryTemplatePrefix) {
		this.categoryTemplatePrefix = categoryTemplatePrefix;
	}
}
