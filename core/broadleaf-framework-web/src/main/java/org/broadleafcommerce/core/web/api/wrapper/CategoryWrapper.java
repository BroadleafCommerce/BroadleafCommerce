/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryAttribute;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.service.CatalogService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *  This is a JAXB wrapper for a Broadleaf Category.  There may be several reasons to extend this class.
 *  First, you may want to extend Broadleaf's CategroryImpl and expose those extensions via a RESTful
 *  service.  You may also want to suppress properties that are being serialized.  To expose new properties, or
 *  suppress properties that have been exposed, do the following:<br/>
 *  <br/>
 *  1. Extend this class   <br/>
 *  2. Override the <code>wrap</code> method. <br/>
 *  3. Within the wrap method, either override all properties that you want to set, or call <code>super.wrap(Category)</code>   <br/>
 *  4. Set additional property values that you have added.  <br/>
 *  5. Set super properties to null if you do not want them serialized. (e.g. <code>super.name = null;</code>  <br/>
 */
@XmlRootElement(name = "category")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CategoryWrapper extends CategorySummaryWrapper implements APIWrapper<Category> {

    @XmlElement
    protected String url;

    @XmlElement
    protected String urlKey;

    @XmlElement
    protected Date activeStartDate;

    @XmlElement
    protected Date activeEndDate;

    @XmlElement(name = "category")
    @XmlElementWrapper(name = "subcategories")
    protected List<CategorySummaryWrapper> subcategories;

    @XmlElement(name = "product")
    @XmlElementWrapper(name = "products")
    protected List<ProductSummaryWrapper> products;

    @XmlElement(name = "categoryAttribute")
    @XmlElementWrapper(name = "categoryAttributes")
    protected List<CategoryAttributeWrapper> categoryAttributes;

    @Override
    public void wrap(Category category, HttpServletRequest request) {
        super.wrap(category, request);
        Integer subcategoryDepth = (Integer) request.getAttribute("subcategoryDepth");
        wrap(category, subcategoryDepth, request);
    }

    protected void wrap(Category category, Integer depth, HttpServletRequest request) {
        super.wrap(category, request);
        this.activeStartDate = category.getActiveStartDate();
        this.activeEndDate = category.getActiveEndDate();
        this.url = category.getUrl();
        this.urlKey = category.getUrlKey();

        if (category.getCategoryAttributes() != null && !category.getCategoryAttributes().isEmpty()) {
            categoryAttributes = new ArrayList<CategoryAttributeWrapper>();
            for (CategoryAttribute attribute : category.getCategoryAttributes()) {
                CategoryAttributeWrapper wrapper = (CategoryAttributeWrapper) context.getBean(CategoryAttributeWrapper.class.getName());
                wrapper.wrap(attribute, request);
                categoryAttributes.add(wrapper);
            }
        }

        Integer productLimit = (Integer) request.getAttribute("productLimit");
        Integer productOffset = (Integer) request.getAttribute("productOffset");
        Integer subcategoryLimit = (Integer) request.getAttribute("subcategoryLimit");
        Integer subcategoryOffset = (Integer) request.getAttribute("subcategoryOffset");

        if (productLimit != null && productOffset == null) {
            productOffset = 1;
        }

        if (subcategoryLimit != null && subcategoryOffset == null) {
            subcategoryOffset = 1;
        }

        if (depth == null) {
            depth = 1;
        }

        if (productLimit != null && productOffset != null) {

            CatalogService catalogService = (CatalogService) context.getBean("blCatalogService");

            List<Product> productList = catalogService.findProductsForCategory(category, productLimit, productOffset);
            if (productList != null && !productList.isEmpty()) {
                if (products == null) {
                    products = new ArrayList<ProductSummaryWrapper>();
                }

                for (Product p: productList) {
                    ProductSummaryWrapper productSummaryWrapper;
                    if (p instanceof ProductBundle) {
                        productSummaryWrapper = (ProductWrapper) context.getBean(ProductBundleWrapper.class.getName());
                    } else {
                        productSummaryWrapper = (ProductSummaryWrapper) context.getBean(ProductSummaryWrapper.class.getName());
                    }
                    productSummaryWrapper.wrap(p, request);
                    products.add(productSummaryWrapper);
                }
            }

        }

        if (subcategoryLimit != null && subcategoryOffset != null && depth != null) {
            subcategories = buildSubcategoryTree(subcategories, category, request, depth);
        }
    }


    protected List<CategorySummaryWrapper> buildSubcategoryTree(List<CategorySummaryWrapper> wrappers, Category root, HttpServletRequest request, int depth) {
        CatalogService catalogService = (CatalogService) context.getBean("blCatalogService");

        if (depth <= 0) {
            return wrappers;
        }

        Integer subcategoryLimit = (Integer) request.getAttribute("subcategoryLimit");
        Integer subcategoryOffset = (Integer) request.getAttribute("subcategoryOffset");

        List<Category> subcategories = catalogService.findAllSubCategories(root, subcategoryLimit, subcategoryOffset);
        if (subcategories !=null && !subcategories.isEmpty() && wrappers == null) {
            wrappers = new ArrayList<CategorySummaryWrapper>();
        }

        for (Category c : subcategories) {
            CategoryWrapper subcategoryWrapper = (CategoryWrapper) context.getBean(CategoryWrapper.class.getName());
            subcategoryWrapper.wrap(c, depth - 1, request);
            wrappers.add(subcategoryWrapper);
        }

        return wrappers;
    }
}
