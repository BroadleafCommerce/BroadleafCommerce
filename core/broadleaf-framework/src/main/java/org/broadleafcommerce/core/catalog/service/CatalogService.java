/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
import org.broadleafcommerce.core.catalog.domain.dto.AssignedProductOptionDTO;
import org.broadleafcommerce.core.catalog.service.type.ProductType;
import org.broadleafcommerce.core.search.domain.SearchCriteria;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CatalogService {

    Product saveProduct(Product product);

    Product findProductById(Long productId);
    
    Product findProductByExternalId(String externalId);

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
    @Deprecated
    List<Product> findActiveProductsByCategory(Category category, Date currentDate);
    
    /**
     * Given a category and a ProudctSearchCriteria, returns the appropriate matching products
     * 
     * @param category
     * @param searchCriteria
     * @return the matching products
     */
    List<Product> findFilteredActiveProductsByCategory(Category category, SearchCriteria searchCriteria);

    /**
     * @deprecated Use {@link #findFilteredActiveProductsByCategory(Category, SearchCriteria)}
     * 
     * @param category
     * @param currentDate
     * @param searchCriteria
     * @return
     */
    List<Product> findFilteredActiveProductsByCategory(Category category, Date currentDate, SearchCriteria searchCriteria);
    
    /**
     * Given a search query and a SearchCriteria, returns the appropriate matching products
     * 
     * @param query
     * @param searchCriteria
     * @return the matching products
     */
    List<Product> findFilteredActiveProductsByQuery(String query, SearchCriteria searchCriteria);

    /**
     * @deprecated Use {@link #findFilteredActiveProductsByCategory(Category, SearchCriteria)}
     */
    List<Product> findFilteredActiveProductsByQuery(String query, Date currentDate, SearchCriteria searchCriteria);

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
    @Deprecated
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

    void removeSku(Sku sku);

    Category findCategoryById(Long categoryId);

    Category findCategoryByExternalId(String externalId);

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

    List<Sku> findAllSkus(int offset, int limit);

    List<Sku> findSkusByIds(List<Long> ids);

    Sku findSkuById(Long skuId);

    Sku findSkuByExternalId(String externalId);

    /**
     * Method to look up a Sku by the Universal Product Code (UPC).
     * 
     * @param upc
     * @return
     */
    Sku findSkuByUpc(String upc);

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

    Long findTotalCategoryCount();

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

    Category findOriginalCategoryByURI(String uri);

    /**
     * Returns a product associated with the passed in URI or null if no Product is
     * mapped to this URI.
     * 
     * @param uri
     * @return
     */    
    Product findProductByURI(String uri);

    Product findOriginalProductByURI(String uri);

    /**
     * Returns a sku associated with the passed in URI or null if no sku is
     * mapped to this URI.
     * 
     * @param uri
     * @return
     */    
    Sku findSkuByURI(String uri);

    /**
     * Returns a list of {@link org.broadleafcommerce.core.catalog.domain.dto.AssignedProductOptionDTO}
     * found for given the productId.
     *
     * @param productId
     * @return
     */
    List<AssignedProductOptionDTO> findAssignedProductOptionsByProductId(Long productId);

    /**
     * Returns a list of {@link org.broadleafcommerce.core.catalog.domain.dto.AssignedProductOptionDTO}
     * found for given the {@link org.broadleafcommerce.core.catalog.domain.Product}.
     *
     * @param product
     * @return
     */
    List<AssignedProductOptionDTO> findAssignedProductOptionsByProduct(Product product);

    Long countProductsUsingProductOptionById(Long productOptionId);

    /**
     * Returns a paginated list of Product Ids that are using the passed in ProductOption ID
     *
     * @param productOptionId
     * @param start
     * @param pageSize
     * @return
     */
    List<Long> findProductIdsUsingProductOptionById(Long productOptionId, int start, int pageSize);

}
