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

package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.service.type.ProductType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CatalogService {

    public Product saveProduct(Product product);    

    public Product findProductById(Long productId);

    public List<Product> findProductsByName(String searchName);

    public List<Product> findActiveProductsByCategory(Category category, Date currentDate);

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

    public List<Category> findAllCategories();

    public List<Product> findAllProducts();

    public List<Product> findProductsForCategory(Category category);

    public Sku saveSku(Sku sku);

    public List<Sku> findAllSkus();

    public List<Sku> findSkusByIds(List<Long> ids);

    public Sku findSkuById(Long skuId);

    public Map<String, List<Long>> getChildCategoryURLMapByCategoryId(Long categoryId);

    public Category createCategory();
    
    public Sku createSku();
    
    public Product createProduct(ProductType productType);

    public List<Category> findAllSubCategories(Category category);

    public List<Category> findActiveSubCategoriesByCategory(Category category);
    
}
