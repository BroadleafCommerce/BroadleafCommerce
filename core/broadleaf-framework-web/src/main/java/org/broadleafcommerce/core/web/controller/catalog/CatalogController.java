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

package org.broadleafcommerce.core.web.controller.catalog;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.FeaturedProduct;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductSku;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.CartService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.core.web.catalog.CatalogSort;
import org.broadleafcommerce.core.web.order.model.AddToCartItem;
import org.broadleafcommerce.core.web.order.model.WishlistRequest;
import org.broadleafcommerce.core.web.search.SearchFilterUtil;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/store")
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

    @ModelAttribute("addToCartItem")
    public AddToCartItem initAddToCartItem() {
        return new AddToCartItem();
    }
    
    private boolean shouldRedirectToUrl(Category currentCategory, Category rootCategory, HttpServletRequest request) {
        if (currentCategory.getUrl() == null || "".equals(currentCategory.getUrl())) {
            return false;
        }
        
        if (currentCategory.getUrl().startsWith("/"+rootCategory.getUrlKey())) {
            return false;
        }
        
        String requestURL = request.getRequestURL().toString().trim();
        String currentCategoryURL = currentCategory.getUrl();

        return (! requestURL.equals(currentCategoryURL));
    }

    private String showCatalog (ModelMap model, HttpServletRequest request, CatalogSort catalogSort) {
        Category rootCategory = findRootCategory();
        addCategoryToModel(request, model, rootCategory);
        boolean productFound = addProductsToModel(request, model, catalogSort);

        String view = defaultCategoryView;
        if (productFound) {
            // TODO: Nice to have: product logic similar to category below
            view = defaultProductView;
        } else {
            Category currentCategory = (Category) model.get("currentCategory");


            // TODO: add (&& ! currentCategory.getUrl().startsWith("/"+ rootCategory.getUrlKey())
            if (shouldRedirectToUrl(currentCategory, rootCategory, request)) {
                return "redirect:"+currentCategory.getUrl();
            } else if (currentCategory.getDisplayTemplate() != null && !"".equals(currentCategory.getDisplayTemplate())) {
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

    protected void addCategoryToModel(HttpServletRequest request, ModelMap model, Category rootCategory) {
        String url = pathHelper.getRequestUri(request).substring(pathHelper.getContextPath(request).length());
        String categoryId = request.getParameter("categoryId");
        if (categoryId != null) {
            Category category = catalogService.findCategoryById(new Long(categoryId));
            if (category != null) {
                url = category.getUrl();
            }
        }

        List<Long> categoryIdList = catalogService.getChildCategoryURLMapByCategoryId(rootCategory.getId()).get(url);
        List<Category> categoryList = null;
        if (categoryIdList != null) {
            categoryList = new ArrayList<Category>(categoryIdList.size());
            for (Long id : categoryIdList) {
                categoryList.add(catalogService.findCategoryById(id));
            }
        }

        addCategoryListToModel(categoryList, rootCategory, url, model);
        model.addAttribute("rootCategory", rootCategory);
    }

    private Category findRootCategory() {
        Category rootCategory = null;
        if (getRootCategoryId() != null) {
            rootCategory = catalogService.findCategoryById(getRootCategoryId());
        } else if (getRootCategoryName() != null) {
            rootCategory = catalogService.findCategoryByName(getRootCategoryName());
        }

        if (rootCategory == null) {
            throw new IllegalStateException("Catalog Controller configured incorrectly - rootId category not found: " + rootCategoryId);
        }
        return rootCategory;
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
                categoryList = new ArrayList<Category>(1);
                categoryList.add(rootCategory);
            } else {
                url = url.substring(0, url.lastIndexOf("/"));
                List<Long> categoryIdList = catalogService.getChildCategoryURLMapByCategoryId(rootCategory.getId()).get(url);
                if (categoryIdList != null) {
                    categoryList = new ArrayList<Category>(categoryIdList.size());
                    for (Long id : categoryIdList) {
                        categoryList.add(catalogService.findCategoryById(id));
                    }
                }
            }
        }
        
        List<Category> siblingCategories  = new ArrayList<Category>();
        Category currentCategory = (Category) categoryList.get(categoryList.size()-1);        
        siblingCategories = currentCategory.getAllChildCategories();
        

        model.addAttribute("breadcrumbCategories", categoryList);
        model.addAttribute("currentCategory", categoryList.get(categoryList.size()-1));
        model.addAttribute("categoryError", categoryError);
        model.addAttribute("displayCategories",siblingCategories);
        

        return categoryError;
    }

    /*
     * This controller method is for demonstration purposes only. It contains a call to
     * catalogService.findActiveProductsByCategory, which may return a large list. A
     * more performant solution would be to utilize data paging techniques.
     */
    protected boolean validateProductAndAddToModel(Product product, ModelMap model) {
        Category currentCategory = (Category) model.get("currentCategory");
        Category rootCategory = (Category) model.get("rootCategory");
        int productPosition=0;

        List<Product> productList = catalogService.findActiveProductsByCategory(currentCategory, SystemTime.asDate());
        if (productList != null) {
            populateProducts(productList, currentCategory);
            model.addAttribute("products", productList);
        }
        productPosition = findProductPositionInList(product, productList);
        if (productPosition == 0) {
            // look for product in its default category and override category from request URL
            currentCategory = product.getDefaultCategory();
            if (currentCategory.isActive()) {
                model.put("currentCategory", currentCategory);
                productList = catalogService.findActiveProductsByCategory(currentCategory, SystemTime.asDate());
                if (productList != null) {
                    model.put("currentProducts", productList);
                }
                String url = currentCategory.getGeneratedUrl();

                // override category list settings using this products default
                List<Long> categoryIdList = catalogService.getChildCategoryURLMapByCategoryId(rootCategory.getId()).get(url);
                List <Category> categoryList = null;
                if (categoryIdList != null) {
                    categoryList = new ArrayList<Category>(categoryIdList.size());
                    for (Long id : categoryIdList) {
                        categoryList.add(catalogService.findCategoryById(id));
                    }
                }
                if (categoryList != null && !addCategoryListToModel(categoryList, rootCategory, url, model)) {
                    productPosition = findProductPositionInList(product, productList);
                }
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

        /*
            private long productId;
    private long categoryId;
    private long skuId;
    private Long orderId;
    private int quantity;
    private Map additionalAttributes;
         */

        AddToCartItem addItemRequest = new AddToCartItem();
        addItemRequest.setCategoryId(currentCategory.getId());
        addItemRequest.setProductId(product.getId());
        if (product.getDefaultSku() != null) {
            addItemRequest.setSkuId(product.getDefaultSku().getId());
        }
        addItemRequest.setQuantity(1);

        /*if (product.getProductOptions() != null) {
            addItemRequest.getProductOptions()
        }
        for ()
        addItemRequest.setAdditionalAttributes();
        addItemRequest.setQuantity(1);
        addItemRequest.setAddSkuId(product.getDefaultSku().getId());
        model.addAttribute("addToCartItem", wishlistRequest);   */

        WishlistRequest wishlistRequest = new WishlistRequest();
        wishlistRequest.setAddCategoryId(currentCategory.getId());
        wishlistRequest.setAddProductId(product.getId());
        wishlistRequest.setQuantity(1);
        wishlistRequest.setAddSkuId(product.getDefaultSku().getId());
        model.addAttribute("wishlistRequest", wishlistRequest);

        return (productPosition !=0);
    }

    /*
     * This controller method is for demonstration purposes only. It contains a call to
     * catalogService.findActiveProductsByCategory, which may return a large list. A
     * more performant solution would be to utilize data paging techniques.
     */
    protected boolean addProductsToModel(HttpServletRequest request, ModelMap model, CatalogSort catalogSort) {
        boolean productFound = false;

        String productId = request.getParameter("productId");
        Product product = null;
        try {
            product = catalogService.findProductById(new Long(productId));
        } catch(Exception e) {
            //If product is not found, return all values in category
        }

        if (product != null && product.isActive()) {
            productFound = validateProductAndAddToModel(product, model);
            addRatingSummaryToModel(productId, model);
        } else {
            Category currentCategory = (Category) model.get("currentCategory");
            List<Product> productList = catalogService.findActiveProductsByCategory(currentCategory, SystemTime.asDate());
            SearchFilterUtil.filterProducts(productList, request.getParameterMap(), new String[] {"manufacturer", "sku.salePrice"});

            if ((catalogSort != null) && (catalogSort.getSort() != null)) {
                populateProducts(productList, currentCategory);
                model.addAttribute("displayProducts", sortProducts(catalogSort, productList));
            }
            else {
                catalogSort = new CatalogSort();
                catalogSort.setSort("featured");
                populateProducts(productList, currentCategory);
                model.addAttribute("displayProducts", sortProducts(catalogSort, productList));
            }
        }

        return productFound;
    }

    private void addRatingSummaryToModel(String productId, ModelMap model) {
        RatingSummary ratingSummary = ratingService.readRatingSummary(productId, RatingType.PRODUCT);
        model.addAttribute("ratingSummary", ratingSummary);
    }

    private void populateProducts (List<Product> productList, Category currentCategory ) {

        for (FeaturedProduct featuredProduct : currentCategory.getFeaturedProducts()) {
            for (Product product: productList) {
                if ((product.equals(featuredProduct.getProduct()))) {
                    product.setPromoMessage(featuredProduct.getPromotionMessage());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Product> sortProducts (CatalogSort catalogSort, List<Product> displayProducts) {
        if (catalogSort.getSort().equals("priceL")) {
            Collections.sort(displayProducts, new BeanComparator("sku.salePrice", new NullComparator()));
        }
        else if (catalogSort.getSort().equals("priceH")) {
            Collections.sort(displayProducts, new ReverseComparator(new BeanComparator("sku.salePrice", new NullComparator())));
        }
        else if (catalogSort.getSort().equals("manufacturerA")) {
            Collections.sort(displayProducts, new BeanComparator("manufacturer", new NullComparator()));
        }
        else if (catalogSort.getSort().equals("manufacturerZ")) {
            Collections.sort(displayProducts, new ReverseComparator(new BeanComparator("manufacturer", new NullComparator())));
        }
        else if (catalogSort.getSort().equals("featured")) {
            Collections.sort(displayProducts, new ReverseComparator(new BeanComparator("promoMessage", new NullComparator())));
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
