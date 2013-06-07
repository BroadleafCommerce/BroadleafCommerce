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

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.util.xml.ISO8601DateAdapter;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryAttribute;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.broadleafcommerce.core.search.domain.ProductSearchResult;
import org.broadleafcommerce.core.search.service.SearchService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
public class CategoryWrapper extends BaseWrapper implements APIWrapper<Category> {

    @XmlElement
    protected CategorySummaryWrapper categorySummary;

    @XmlElement
    protected String url;

    @XmlElement
    protected String urlKey;

    @XmlElement
    @XmlJavaTypeAdapter(ISO8601DateAdapter.class)
    protected Date activeStartDate;

    @XmlElement
    @XmlJavaTypeAdapter(ISO8601DateAdapter.class)
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

        this.categorySummary = (CategorySummaryWrapper) context.getBean(CategorySummaryWrapper.class.getName());
        this.categorySummary.wrap(category, request);

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

        if (productLimit != null && productOffset != null) {
            SearchService searchService = (SearchService) context.getBean("blSearchService");
            ProductSearchCriteria searchCriteria = new ProductSearchCriteria();
            searchCriteria.setPage(productOffset);
            searchCriteria.setPageSize(productLimit);
            searchCriteria.setFilterCriteria(new HashMap<String, String[]>());
            try {
                ProductSearchResult result = searchService.findExplicitProductsByCategory(category, searchCriteria);
                List<Product> productList = result.getProducts();
                if (productList != null && !productList.isEmpty()) {
                    if (products == null) {
                        products = new ArrayList<ProductSummaryWrapper>();
                    }

                    for (Product p : productList) {
                        ProductSummaryWrapper productSummaryWrapper = (ProductSummaryWrapper) context.getBean(ProductSummaryWrapper.class.getName());
                        productSummaryWrapper.wrap(p, request);
                        products.add(productSummaryWrapper);
                    }
                }
            } catch (ServiceException e) {
                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .type(MediaType.TEXT_PLAIN).entity("An unexpected error occured  " + e.getMessage()).build());
            }
        }

        if (subcategoryLimit != null && subcategoryOffset != null) {
            subcategories = buildSubcategoryTree(subcategories, category, request);
        }
    }


    protected List<CategorySummaryWrapper> buildSubcategoryTree(List<CategorySummaryWrapper> wrappers, Category root, HttpServletRequest request) {
        CatalogService catalogService = (CatalogService) context.getBean("blCatalogService");

        Integer subcategoryLimit = (Integer) request.getAttribute("subcategoryLimit");
        Integer subcategoryOffset = (Integer) request.getAttribute("subcategoryOffset");

        List<Category> subcategories = catalogService.findActiveSubCategoriesByCategory(root, subcategoryLimit, subcategoryOffset);
        if (subcategories !=null && !subcategories.isEmpty() && wrappers == null) {
            wrappers = new ArrayList<CategorySummaryWrapper>();
        }

        for (Category c : subcategories) {
            CategorySummaryWrapper subcategoryWrapper = (CategorySummaryWrapper) context.getBean(CategorySummaryWrapper.class.getName());
            subcategoryWrapper.wrap(c, request);
            wrappers.add(subcategoryWrapper);
        }

        return wrappers;
    }
}
