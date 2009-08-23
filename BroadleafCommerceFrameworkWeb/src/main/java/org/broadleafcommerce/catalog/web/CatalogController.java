/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.catalog.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.FeaturedProduct;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.order.web.model.WishlistRequest;
import org.broadleafcommerce.profile.web.CustomerState;
import org.broadleafcommerce.rating.domain.RatingSummary;
import org.broadleafcommerce.rating.service.RatingService;
import org.broadleafcommerce.rating.service.type.RatingType;
import org.broadleafcommerce.search.util.SearchFilterUtil;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UrlPathHelper;

@Controller
public class CatalogController {

	//TODO Instead of mixing and matching - we should prob be autowiring all the dependencies for this controller.
    @Resource(name="blCartService")
    protected CartService cartService;
    @Resource(name="blCustomerState")
    protected CustomerState customerState;
    
    private final UrlPathHelper pathHelper = new UrlPathHelper();
    private CatalogService catalogService;
    private RatingService ratingService;
    private String defaultCategoryView;
    private String defaultProductView;
    private Long rootCategoryId;
    private String rootCategoryName;
    private String categoryTemplatePrefix;

    @RequestMapping(method =  {RequestMethod.GET})
    public String viewCatalog(ModelMap model, HttpServletRequest request) {
        return showCatalog(model, request, null);
    }

    private String showCatalog (ModelMap model, HttpServletRequest request, CatalogSort catalogSort) {
        addCategoryToModel(request, model);
        boolean productFound = addProductsToModel(request, model, catalogSort);

        String view = defaultCategoryView;
        if (productFound) {
            // TODO: Nice to have: product logic similar to category below
            view = defaultProductView;
        } else {
            Category currentCategory = (Category) model.get("currentCategory");
            if (currentCategory.getUrl() != null && !"".equals(currentCategory.getUrl())) {
                return "redirect:"+currentCategory.getUrl();
            } else if (currentCategory.getDisplayTemplate() != null && !"".equals(currentCategory.getUrl())) {
                view = categoryTemplatePrefix + currentCategory.getDisplayTemplate();
            } else {
                if ("true".equals(request.getParameter("ajax"))) {
                    view = "catalog/categoryView/mainContentFragment";
                } else {
                    view = defaultCategoryView;
                }
            }
        }

        if (catalogSort == null) {
            model.addAttribute("catalogSort", new CatalogSort());
        }

        List<Order> wishlists = cartService.findOrdersForCustomer(customerState.getCustomer(request), OrderStatus.NAMED);
        model.addAttribute("wishlists", wishlists);

        return view;
    }

    @RequestMapping(method =  {RequestMethod.POST})
    public String sortCatalog (ModelMap model, HttpServletRequest request, @ModelAttribute CatalogSort catalogSort) {
        return showCatalog(model, request, catalogSort);
    }

    protected void addCategoryToModel(HttpServletRequest request, ModelMap model ) {
        Category rootCategory = null;
        if (getRootCategoryId() != null) {
            rootCategory = catalogService.findCategoryById(getRootCategoryId());
        } else if (getRootCategoryName() != null) {
            rootCategory = catalogService.findCategoryByName(getRootCategoryName());
        }

        if (rootCategory == null) {
            throw new IllegalStateException("Catalog Controller configured incorrectly - root category not found: " + rootCategoryId);
        }

        String url = pathHelper.getRequestUri(request).substring(pathHelper.getContextPath(request).length());
        String categoryId = request.getParameter("categoryId");
        if (categoryId != null) {
            Category category = catalogService.findCategoryById(new Long(categoryId));
            if (category != null) {
                url = category.getUrl();
            }
        }

        List<Category> categoryList = rootCategory.getChildCategoryURLMap().get(url);

        for (Category category : categoryList) {
            category = catalogService.findCategoryById(category.getId());
        }
        addCategoryListToModel(categoryList, rootCategory, url, model);
        model.addAttribute("rootCategory", rootCategory);
    }

    protected int findProductPositionInList(Product product, List<Product> products) {
        for (int i=0; i < products.size(); i++) {
            Product currentProduct = products.get(i);
            if (product.getId().equals(currentProduct.getId())) {
                return i+1;
            }
        }
        return 0;
    }

    protected boolean addCategoryListToModel(List<Category> categoryList, Category rootCategory, String url, ModelMap model) {
        boolean categoryError = false;

        while (categoryList == null) {
            categoryError = true;

            int pos = url.indexOf("/");
            if (pos == -1) {
                categoryList = new ArrayList<Category>();
                categoryList.add(rootCategory);
            } else {
                url = url.substring(0, url.lastIndexOf("/"));
                categoryList = rootCategory.getChildCategoryURLMap().get(url);
            }
        }

        model.addAttribute("breadcrumbCategories", categoryList);
        model.addAttribute("currentCategory", categoryList.get(categoryList.size()-1));
        model.addAttribute("categoryError", categoryError);

        return categoryError;
    }

    protected boolean validateProductAndAddToModel(Product product, ModelMap model) {
        Category currentCategory = (Category) model.get("currentCategory");
        Category rootCategory = (Category) model.get("rootCategory");
        int productPosition=0;

        List<Product> productList = catalogService.findActiveProductsByCategory(currentCategory);
        if (productList != null) {
            model.addAttribute("displayProducts", populateProducts(productList, currentCategory));
        }
        productPosition = findProductPositionInList(product, productList);
        if (productPosition == 0) {
            // look for product in its default category and override category from request URL
            currentCategory = product.getDefaultCategory();
            productList = catalogService.findActiveProductsByCategory(currentCategory);
            if (productList != null) {
                model.addAttribute("displayProducts", populateProducts(productList, currentCategory));
            }
            String url = currentCategory.getGeneratedUrl();

            // override category list settings using this products default
            List<Category> categoryList = rootCategory.getChildCategoryURLMap().get(url);
            if (categoryList != null && ! addCategoryListToModel(categoryList, rootCategory, url, model)) {
                productPosition = findProductPositionInList(product, productList);
            }
        }

        if (productPosition != 0) {
            model.addAttribute("productError", false);
            model.addAttribute("currentProduct", product);
            model.addAttribute("productPosition", productPosition);
            if (productPosition != 1) {
                model.addAttribute("previousProduct", productList.get(productPosition-2));
            }
            if (productPosition < productList.size()) {
                model.addAttribute("nextProduct", productList.get(productPosition));
            }
            model.addAttribute("totalProducts", productList.size());
        } else {
            model.addAttribute("productError", true);
        }

        WishlistRequest wishlistRequest = new WishlistRequest();
        wishlistRequest.setAddCategoryId(currentCategory.getId());
        wishlistRequest.setAddProductId(product.getId());
        wishlistRequest.setQuantity(1);
        wishlistRequest.setAddSkuId(product.getSkus().get(0).getId());
        model.addAttribute("wishlistRequest", wishlistRequest);

        return (productPosition !=0);
    }

    @SuppressWarnings("unchecked")
    protected boolean addProductsToModel(HttpServletRequest request, ModelMap model, CatalogSort catalogSort) {
        boolean productFound = false;

        String productId = request.getParameter("productId");
        if (productId != null) {
            Product product = catalogService.findProductById(new Long(productId));
            if (product != null) {
                productFound = validateProductAndAddToModel(product, model);
                addRatingSummaryToModel(productId, model);
            }
        } else {
            Category currentCategory = (Category) model.get("currentCategory");
            List<Product> productList = catalogService.findActiveProductsByCategory(currentCategory);
            SearchFilterUtil.filterProducts(productList, request.getParameterMap(), new String[] {"manufacturer", "skus[0].salePrice"});

            if ((catalogSort != null) && (catalogSort.getSort() != null)) {
                List<DisplayProduct> displayProducts = new ArrayList<DisplayProduct>();
                displayProducts = populateProducts(productList, currentCategory);
                model.addAttribute("displayProducts", sortProducts(catalogSort, displayProducts));
            }
            else {
                catalogSort = new CatalogSort();
                catalogSort.setSort("featured");
                List<DisplayProduct> displayProducts = new ArrayList<DisplayProduct>();
                displayProducts = populateProducts(productList, currentCategory);
                model.addAttribute("displayProducts", sortProducts(catalogSort, displayProducts));
            }
        }

        return productFound;
    }

    private void addRatingSummaryToModel(String productId, ModelMap model) {
        RatingSummary ratingSummary = ratingService.readRatingSummary(productId, RatingType.PRODUCT);
        model.addAttribute("ratingSummary", ratingSummary);
    }

    private List<DisplayProduct> populateProducts (List<Product> productList, Category currentCategory ) {
        List<DisplayProduct> displayProducts = new ArrayList<DisplayProduct>();

        for (Product product : productList) {
            DisplayProduct displayProduct = new DisplayProduct();
            displayProduct.setProduct(product);
            displayProducts.add(displayProduct);
        }

        for (FeaturedProduct featuredProduct : currentCategory.getFeaturedProducts()) {
            for (DisplayProduct displayProduct: displayProducts) {
                if ((displayProduct.getProduct().equals(featuredProduct.getProduct()))) {
                    displayProduct.setPromoMessage(featuredProduct.getPromotionMessage());
                }
            }
        }

        return displayProducts;
    }

    @SuppressWarnings("unchecked")
    private List<DisplayProduct> sortProducts (CatalogSort catalogSort, List<DisplayProduct> displayProducts) {
        if (catalogSort.getSort().equals("priceL")) {
            Collections.sort(displayProducts, new BeanComparator("product.skus[0].salePrice"));
        }
        else if (catalogSort.getSort().equals("priceH")) {
            Collections.sort(displayProducts, new ReverseComparator(new BeanComparator("product.skus[0].salePrice")));
        }
        else if (catalogSort.getSort().equals("manufacturerA")) {
            Collections.sort(displayProducts, new BeanComparator("product.manufacturer"));
        }
        else if (catalogSort.getSort().equals("manufacturerZ")) {
            Collections.sort(displayProducts, new ReverseComparator(new BeanComparator("product.manufacturer")));
        }
        else if (catalogSort.getSort().equals("featured")) {
            Collections.sort(displayProducts, new ReverseComparator(new BeanComparator("promoMessage")));
        }

        return displayProducts;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, false));
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, false));
    }

    public Long getRootCategoryId() {
        return rootCategoryId;
    }

    public String getRootCategoryName() {
        return rootCategoryName;
    }

    public void setRootCategoryId(Long rootCategoryId) {
        this.rootCategoryId = rootCategoryId;
    }

    public void setRootCategoryName(String rootCategoryName) {
        this.rootCategoryName = rootCategoryName;
    }

    public CatalogService getCatalogService() {
        return catalogService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public RatingService getRatingService() {
        return ratingService;
    }

    public void setRatingService(RatingService ratingService) {
        this.ratingService = ratingService;
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
