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

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.CategoryDao;
import org.broadleafcommerce.catalog.dao.ProductDao;
import org.broadleafcommerce.catalog.dao.SkuDao;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("blCatalogService")
public class CatalogServiceImpl implements CatalogService {

    @Resource
    protected CategoryDao categoryDao;

    @Resource
    protected ProductDao productDao;

    @Resource
    protected SkuDao skuDao;

    public Product findProductById(Long productId) {
        return productDao.readProductById(productId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Product> findProductsByName(String searchName) {
        return productDao.readProductsByName(searchName);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Product> findActiveProductsByCategory(Category category) {
        return productDao.readActiveProductsByCategory(category.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Product saveProduct(Product product) {
        return productDao.save(product);
    }

    @Override
    public Category findCategoryById(Long categoryId) {
        return categoryDao.readCategoryById(categoryId);
    }

    @Override
    public Category findCategoryByName(String categoryName) {
        return categoryDao.readCategoryByName(categoryName);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Category saveCategory(Category category) {
        return categoryDao.save(category);
    }

    @Override
    public List<Category> findAllCategories() {
        return categoryDao.readAllCategories();
    }

    @Override
    public List<Product> findAllProducts() {
        return categoryDao.readAllProducts();
    }

    @Override
    public List<Sku> findAllSkus() {
        return skuDao.readAllSkus();
    }

    @Override
    public Sku findSkuById(Long skuId) {
        return skuDao.readSkuById(skuId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Sku saveSku(Sku sku) {
        return skuDao.save(sku);
    }

    @Override
    public List<Sku> findSkusByIds(List<Long> ids) {
        return skuDao.readSkusById(ids);
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void setSkuDao(SkuDao skuDao) {
        this.skuDao = skuDao;
    }

    public List<Product> findProductsForCategory(Category category) {
        // TODO Implement this, it's being called by TCS CartController
        return null;
    }

    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }
}
