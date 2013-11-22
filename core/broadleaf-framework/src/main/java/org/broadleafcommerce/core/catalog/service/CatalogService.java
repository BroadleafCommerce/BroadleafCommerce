/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuFee;
import org.broadleafcommerce.core.catalog.service.type.ProductType;
import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CatalogService {

    Product saveProduct(Product product);

    Product findProductById(Long productId);

    List<Product> findProductsByName(String searchName);

    /**
     * Find a subset of {@code Product} instances whose name starts with
     * or is equal to the passed in search parameter.  Res
     * @param searchName
     * @param limit the maximum number of results
     * @param offset the starting point in the record set
     * @return the list of product instances that fit the search criteria
     */
    List<Product> findProductsByName(String searchName, int limit, int offset);

    List<Product> findActiveProductsByCategory(Category category);

    /**
     * @deprecated Use findActiveProductsByCategory
     * 
     * @param category
     * @param currentDate
     * @return
     */
    List<Product> findActiveProductsByCategory(Category category, Date currentDate);
    
    /**
     * Given a category and a ProudctSearchCriteria, returns the appropriate matching products
     * 
     * @param category
     * @param searchCriteria
     * @return the matching products
     */
    List<Product> findFilteredActiveProductsByCategory(Category category, ProductSearchCriteria searchCriteria);

    /**
     * @deprecated Use {@link #findFilteredActiveProductsByCategory(Category, ProductSearchCriteria)}
     * 
     * @param category
     * @param currentDate
     * @param searchCriteria
     * @return
     */
    List<Product> findFilteredActiveProductsByCategory(Category category, Date currentDate, ProductSearchCriteria searchCriteria);
    
    /**
     * Given a search query and a ProductSearchCriteria, returns the appropriate matching products
     * 
     * @param query
     * @param searchCriteria
     * @return the matching products
     */
    List<Product> findFilteredActiveProductsByQuery(String query, ProductSearchCriteria searchCriteria);

    /**
     * @deprecated Use {@link #findFilteredActiveProductsByCategory(Category, ProductSearchCriteria)}
     */
    public List<Product> findFilteredActiveProductsByQuery(String query, Date currentDate, ProductSearchCriteria searchCriteria);

    /**
     * Same as {@link #findActiveProductsByCategory(Category)} but allowing for pagination.
     * 
     * @param category
     * @param limit
     * @param offset
     * @return
     */
    List<Product> findActiveProductsByCategory(Category category, int limit, int offset);

    /**
     * @deprecated Use {@link #findActiveProductsByCategory(Category, limit, offset}
     */
    List<Product> findActiveProductsByCategory(Category category, Date currentDate, int limit, int offset);

    /**
     * Find all ProductBundles whose automatic attribute is set to true.
     *
     * Automatic product bundles are collections of products that can receive special
     * pricing.  With automatic product bundles, if a customer adds all of the
     * components of the bundle individually to the cart, they will automatically get
     * assembeled into a bundle.
     *
     * @return
     */
    List<ProductBundle> findAutomaticProductBundles();


    Category saveCategory(Category category);
    
    void removeCategory(Category category);

    void removeProduct(Product product);

    Category findCategoryById(Long categoryId);

    /**
     * Retrieve a {@code Category} instance based on its name property.
     *
     * Broadleaf allows more than one category to have the same name. Calling
     * this method could produce an exception in such situations. Use
     * {@link #findCategoriesByName(String)} instead.
     *
     * @param categoryName the category name to search by
     * @return the Category instance matching the categoryName
     */
    @Deprecated
    Category findCategoryByName(String categoryName);

    /**
     * Retrieve a list of {@code Category} instance based on the name
     * property.
     *
     * @param categoryName the category name to search by
     * @return the list of matching Category instances
     */
    List<Category> findCategoriesByName(String categoryName);

    /**
     * Retrieve a list of {@code Category} instances based on the search criteria
     *
     * @param categoryName the name of the category to search by
     * @param limit the maximum number of results to return
     * @param offset the starting point of the records to return
     * @return a list of category instances that match the search criteria
     */
    List<Category> findCategoriesByName(String categoryName, int limit, int offset);

    List<Category> findAllCategories();

    List<Category> findAllCategories(int limit, int offset);

    List<Product> findAllProducts();

    List<Product> findAllProducts(int limit, int offset);

    List<Product> findProductsForCategory(Category category);

    List<Product> findProductsForCategory(Category category, int limit, int offset);

    Sku saveSku(Sku sku);
    
    SkuFee saveSkuFee(SkuFee fee);

    List<Sku> findAllSkus();

    List<Sku> findSkusByIds(List<Long> ids);

    Sku findSkuById(Long skuId);

    /**
     * Get a hierarchical map of all child categories keyed on the url
     *
     * @param categoryId the parent category to which the children belong
     * @return hierarchical map of all child categories
     * @deprecated this approach is inherently inefficient - don't use.
     */
    @Deprecated
    Map<String, List<Long>> getChildCategoryURLMapByCategoryId(Long categoryId);

    Category createCategory();
    
    Sku createSku();
    
    Product createProduct(ProductType productType);

    List<Category> findAllParentCategories();
    
    List<Category> findAllSubCategories(Category category);

    List<Category> findAllSubCategories(Category category, int limit, int offset);

    List<Category> findActiveSubCategoriesByCategory(Category category);

    List<Category> findActiveSubCategoriesByCategory(Category category, int limit, int offset);
    
    List<ProductOption> readAllProductOptions();
    
    ProductOption saveProductOption(ProductOption option);
    
    ProductOption findProductOptionById(Long productOptionId);
    
    ProductOptionValue findProductOptionValueById(Long productOptionValueId);
    
    /**
     * Returns a category associated with the passed in URI or null if no Category is
     * mapped to this URI.
     * 
     * @param uri
     * @return
     */
    Category findCategoryByURI(String uri);
    
    /**
     * Returns a product associated with the passed in URI or null if no Product is
     * mapped to this URI.
     * 
     * @param uri
     * @return
     */    
    Product findProductByURI(String uri);

}
