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

    public Product saveProduct(Product product);

    public Product findProductById(Long productId);

    public List<Product> findProductsByName(String searchName);

    /**
     * Find a subset of {@code Product} instances whose name starts with
     * or is equal to the passed in search parameter.  Res
     * @param searchName
     * @param limit the maximum number of results
     * @param offset the starting point in the record set
     * @return the list of product instances that fit the search criteria
     */
    public List<Product> findProductsByName(String searchName, int limit, int offset);

    public List<Product> findActiveProductsByCategory(Category category, Date currentDate);
    
    /**
     * Given a category and a ProudctSearchCriteria, returns the appropriate matching products
     * 
     * @param category
     * @param currentDate
     * @param searchCriteria
     * @return the matching products
     */
    public List<Product> findFilteredActiveProductsByCategory(Category category, Date currentDate, ProductSearchCriteria searchCriteria);
    
    /**
     * Given a search query and a ProductSearchCriteria, returns the appropriate matching products
     * 
     * @param query
     * @param currentDate
     * @param searchCriteria
     * @return the matching products
     */
    public List<Product> findFilteredActiveProductsByQuery(String query, Date currentDate, ProductSearchCriteria searchCriteria);

    public List<Product> findActiveProductsByCategory(Category category, Date currentDate, int limit, int offset);

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
    public List<ProductBundle> findAutomaticProductBundles();


    public Category saveCategory(Category category);
    
    public void removeCategory(Category category);

    public Category findCategoryById(Long categoryId);

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
    public Category findCategoryByName(String categoryName);

    /**
     * Retrieve a list of {@code Category} instance based on the name
     * property.
     *
     * @param categoryName the category name to search by
     * @return the list of matching Category instances
     */
    public List<Category> findCategoriesByName(String categoryName);

    /**
     * Retrieve a list of {@code Category} instances based on the search criteria
     *
     * @param categoryName the name of the category to search by
     * @param limit the maximum number of results to return
     * @param offset the starting point of the records to return
     * @return a list of category instances that match the search criteria
     */
    public List<Category> findCategoriesByName(String categoryName, int limit, int offset);

    public List<Category> findAllCategories();

    public List<Category> findAllCategories(int limit, int offset);

    public List<Product> findAllProducts();

    public List<Product> findAllProducts(int limit, int offset);

    public List<Product> findProductsForCategory(Category category);

    public List<Product> findProductsForCategory(Category category, int limit, int offset);

    public Sku saveSku(Sku sku);
    
    public SkuFee saveSkuFee(SkuFee fee);

    public List<Sku> findAllSkus();

    public List<Sku> findSkusByIds(List<Long> ids);

    public Sku findSkuById(Long skuId);

    /**
     * Get a hierarchical map of all child categories keyed on the url
     *
     * @param categoryId the parent category to which the children belong
     * @return hierarchical map of all child categories
     * @deprecated this approach is inherently inefficient - don't use.
     */
    @Deprecated
    public Map<String, List<Long>> getChildCategoryURLMapByCategoryId(Long categoryId);

    public Category createCategory();
    
    public Sku createSku();
    
    public Product createProduct(ProductType productType);

    public List<Category> findAllParentCategories();
    
    public List<Category> findAllSubCategories(Category category);

    public List<Category> findAllSubCategories(Category category, int limit, int offset);

    public List<Category> findActiveSubCategoriesByCategory(Category category);

    public List<Category> findActiveSubCategoriesByCategory(Category category, int limit, int offset);
    
    public List<ProductOption> readAllProductOptions();
    
    public ProductOption saveProductOption(ProductOption option);
    
    public ProductOption findProductOptionById(Long productOptionId);
    
    public ProductOptionValue findProductOptionValueById(Long productOptionValueId);
    
    /**
     * Returns a category associated with the passed in URI or null if no Category is
     * mapped to this URI.
     * 
     * @param uri
     * @return
     */
    public Category findCategoryByURI(String uri);
    
    /**
     * Returns a product associated with the passed in URI or null if no Product is
     * mapped to this URI.
     * 
     * @param uri
     * @return
     */    
    public Product findProductByURI(String uri);

}
