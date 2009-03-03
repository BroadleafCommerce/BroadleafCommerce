package org.broadleafcommerce.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

public class CatalogController extends AbstractController {
	private static final int MAX_LEVELS = 10; // protection against recursive category structure
	protected final Log logger = LogFactory.getLog(getClass());
	private final UrlPathHelper pathHelper = new UrlPathHelper();
	private CatalogService catalogService;
	private String defaultCategoryView;
	private String defaultProductView;
	private Long defaultCategoryId;
	private String categoryTemplatePrefix;

	@Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) {

    	HashMap<String,Object> model = new HashMap<String,Object>();
        List<Category> breadcrumbCategories = new ArrayList<Category>();
        boolean categoryError = buildBreadcrumbCategories(request, breadcrumbCategories);
        boolean productError = false;
        boolean productFound = false;
        String view = defaultCategoryView;

        String productId = request.getParameter("productId");
        if (productId != null) {
        	Product product = catalogService.findProductById(new Long(productId));
        	// TODO: Validate that product exists in a valid category (e.g. to exclude christmas products)
        	if (product != null) {
        		model.put("currentProduct", product);
        		view = defaultProductView;
        		productFound = true;
        		if (categoryError) {
        			categoryError = buildBreadcrumbCategoriesFromProduct(product, breadcrumbCategories);
        		}
        	} else {
        		productError = true;
        	}
        }

       	model.put("categoryError", categoryError);
       	model.put("productError", productError);

       	Category currentCategory = null;
        if (breadcrumbCategories.size() > 0) {
        	currentCategory = breadcrumbCategories.get(breadcrumbCategories.size()-1);
        } else {
        	currentCategory = catalogService.findCategoryById(defaultCategoryId);
        }

    	model.put("currentCategory", currentCategory);
    	model.put("breadcrumbCategories", breadcrumbCategories);

    	if (productFound) {
    		// TODO: Add logic similar to category below
    		view = defaultProductView;
    	} else {
    		if (currentCategory.getUrl() != null) {
    			view = currentCategory.getUrl();
    		} else if (currentCategory.getDisplayTemplate() != null) {
    			view = categoryTemplatePrefix + currentCategory.getDisplayTemplate();
    		} else {
    			view = defaultCategoryView;
    		}
    	}
        return new ModelAndView(view, model);
    }

	protected boolean buildBreadcrumbCategories(HttpServletRequest request,
			List<Category> categoryList) {
		Map<String, Category> urlKeyCategoryMap = catalogService
				.getCategoryUrlKeyMap();

		String path = pathHelper.getRequestUri(request).substring(
				pathHelper.getContextPath(request).length());
		String[] tokens = StringUtils.split(path, "/");

		boolean categoryError = false;
		if (tokens.length >= 2) {
			Category tmpCategory = null;

			// index starts at 1 to skip the first path value (e.g. shop/browse)
			for (int i = 1; i < tokens.length; i++) {
				tmpCategory = urlKeyCategoryMap.get(tokens[i]);
				if (tmpCategory != null && tmpCategory.isActive()) {
					categoryList.add(tmpCategory);
				} else {
					categoryError = true;
					break;
				}
			}
		}

		return categoryError;
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

	protected Product determineCurrentProduct(HttpServletRequest request,
			HttpServletResponse response) {
		String productId = request.getParameter("productId");
		return catalogService.findProductById(new Long(productId));
	}

	public Long getDefaultCategoryId() {
		return defaultCategoryId;
	}

	public void setDefaultCategoryId(Long defaultCategoryId) {
		this.defaultCategoryId = defaultCategoryId;
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
