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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.search.util.SearchFilterUtil;
import org.broadleafcommerce.time.SystemTime;
import org.broadleafcommerce.web.ConfigurableRedirectView;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.util.UrlPathHelper;

public class CatalogController extends AbstractController {

    private final UrlPathHelper pathHelper = new UrlPathHelper();
    private CatalogService catalogService;
    private String defaultCategoryView;
    private String defaultProductView;
    private Long rootCategoryId;
    private String rootCategoryName;
    private String categoryTemplatePrefix;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {

        HashMap<String, Object> model = new HashMap<String, Object>();

        addCategoryToModel(request, model);
        boolean productFound = addProductsToModel(request, model);

        String view = defaultCategoryView;
        if (productFound) {
            // TODO: Nice to have: product logic similar to category below
            view = defaultProductView;
        } else {
            Category currentCategory = (Category) model.get("currentCategory");
            if (currentCategory.getUrl() != null && !"".equals(currentCategory.getUrl())) {
                ModelAndView modelAndView = new ModelAndView();
                ConfigurableRedirectView redirectView = new ConfigurableRedirectView(currentCategory.getUrl());
                redirectView.setResponseStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                modelAndView.setView(redirectView);
                return modelAndView;
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
        return new ModelAndView(view, model);
    }

    protected void addCategoryToModel(HttpServletRequest request, Map<String, Object> model) {
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
        List<Category> categoryList = catalogService.getChildCategoryURLMapByCategoryId(rootCategory.getId()).get(url);

        addCategoryListToModel(categoryList, rootCategory, url, model);
        model.put("rootCategory", rootCategory);
    }

    protected int findProductPositionInList(Product product, List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            Product currentProduct = products.get(i);
            if (product.getId().equals(currentProduct.getId())) {
                return i + 1;
            }
        }
        return 0;
    }

    protected boolean addCategoryListToModel(List<Category> categoryList, Category rootCategory, String url, Map<String, Object> model) {
        boolean categoryError = false;

        while (categoryList == null) {
            categoryError = true;

            int pos = url.indexOf("/");
            if (pos == -1) {
                categoryList = new ArrayList<Category>();
                categoryList.add(rootCategory);
            } else {
                url = url.substring(0, url.lastIndexOf("/"));
                categoryList = catalogService.getChildCategoryURLMapByCategoryId(rootCategory.getId()).get(url);
            }
        }

        model.put("breadcrumbCategories", categoryList);
        model.put("currentCategory", categoryList.get(categoryList.size() - 1));
        model.put("categoryError", categoryError);
        return categoryError;
    }

    protected boolean validateProductAndAddToModel(Product product, Map<String, Object> model) {
        Category currentCategory = (Category) model.get("currentCategory");
        Category rootCategory = (Category) model.get("rootCategory");
        int productPosition = 0;

        List<Product> productList = catalogService.findActiveProductsByCategory(currentCategory, SystemTime.asDate());
        if (productList != null) {
            model.put("currentProducts", productList);
        }
        productPosition = findProductPositionInList(product, productList);
        if (productPosition == 0) {
            // look for product in its default category and override category
            // from request URL
            currentCategory = product.getDefaultCategory();
            if (currentCategory.isActive()) {
                model.put("currentCategory", currentCategory);
                productList = catalogService.findActiveProductsByCategory(currentCategory, SystemTime.asDate());
                if (productList != null) {
                    model.put("currentProducts", productList);
                }
                String url = currentCategory.getGeneratedUrl();

                // override category list settings using this products default
                List<Category> categoryList = catalogService.getChildCategoryURLMapByCategoryId(rootCategory.getId()).get(url);
                if (categoryList != null && !addCategoryListToModel(categoryList, rootCategory, url, model)) {
                    productPosition = findProductPositionInList(product, productList);
                }
            }
        }

        if (productPosition != 0) {
            model.put("productError", false);
            model.put("currentProduct", product);
            model.put("productPosition", productPosition);
            if (productPosition != 1) {
                model.put("previousProduct", productList.get(productPosition - 2));
            }
            if (productPosition < productList.size()) {
                model.put("nextProduct", productList.get(productPosition));
            }
            model.put("totalProducts", productList.size());
        } else {
            model.put("productError", true);
        }

        return (productPosition != 0);
    }

    @SuppressWarnings("unchecked")
    protected boolean addProductsToModel(HttpServletRequest request, Map<String, Object> model) {
        boolean productFound = false;

        String productId = request.getParameter("productId");
        Product product = null;
        try {
            product = catalogService.findProductById(new Long(productId));
        }
        catch(Exception e) {
            //If product is not found, return all values in category
        }

        if (product != null && product.isActive()) {
            productFound = validateProductAndAddToModel(product, model);
        }
        else {
            Category currentCategory = (Category) model.get("currentCategory");
            List<Product> productList = catalogService.findActiveProductsByCategory(currentCategory, SystemTime.asDate());
            SearchFilterUtil.filterProducts(productList, request.getParameterMap(), new String[] { "manufacturer", "skus[0].salePrice" });
            model.put("currentProducts", productList);
        }

        return productFound;
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
