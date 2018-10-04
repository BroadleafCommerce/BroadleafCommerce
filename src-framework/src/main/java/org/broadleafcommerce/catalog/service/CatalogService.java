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
package org.broadleafcommerce.catalog.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;

public interface CatalogService {

    public Product saveProduct(Product product);

    public Product findProductById(Long productId);

    public List<Product> findProductsByName(String searchName);

    public List<Product> findActiveProductsByCategory(Category category, Date currentDate);

    public Category saveCategory(Category category);

    public Category findCategoryById(Long categoryId);

    public Category findCategoryByName(String categoryName);

    public List<Category> findAllCategories();

    public List<Product> findAllProducts();

    public List<Product> findProductsForCategory(Category category);

    public Sku saveSku(Sku sku);

    public List<Sku> findAllSkus();

    public List<Sku> findSkusByIds(List<Long> ids);

    public Sku findSkuById(Long skuId);

    public Map<String, List<Category>> getChildCategoryURLMapByCategoryId(Long categoryId);

}
