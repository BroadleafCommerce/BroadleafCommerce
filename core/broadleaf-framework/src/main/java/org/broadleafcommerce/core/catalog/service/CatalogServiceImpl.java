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

import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.dao.CategoryDao;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.dao.ProductOptionDao;
import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductBundle;
import org.broadleafcommerce.core.catalog.domain.ProductBundleComparator;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuFee;
import org.broadleafcommerce.core.catalog.service.type.ProductType;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@Service("blCatalogService")
public class CatalogServiceImpl implements CatalogService {

    @Resource(name="blCategoryDao")
    protected CategoryDao categoryDao;

    @Resource(name="blProductDao")
    protected ProductDao productDao;

    @Resource(name="blSkuDao")
    protected SkuDao skuDao;
    
    @Resource(name="blProductOptionDao")
    protected ProductOptionDao productOptionDao;

    @Resource(name = "blCatalogServiceExtensionManager")
    protected CatalogServiceExtensionManager extensionManager;

    @Override
    public Product findProductById(Long productId) {
        return productDao.readProductById(productId);
    }

    @Override
    public List<Product> findProductsByName(String searchName) {
        return productDao.readProductsByName(searchName);
    }

    @Override
    public List<Product> findProductsByName(String searchName, int limit, int offset) {
        return productDao.readProductsByName(searchName, limit, offset);
    }

    @Override
    public List<Product> findActiveProductsByCategory(Category category) {
        return productDao.readActiveProductsByCategory(category.getId());
    }

    @Override
    public List<Product> findFilteredActiveProductsByCategory(Category category, SearchCriteria searchCriteria) {
        return productDao.readFilteredActiveProductsByCategory(category.getId(), searchCriteria);
    }

    @Override
    public List<Product> findFilteredActiveProductsByQuery(String query, SearchCriteria searchCriteria) {
        return productDao.readFilteredActiveProductsByQuery(query, searchCriteria);
    }

    @Override
    public List<Product> findActiveProductsByCategory(Category category, int limit, int offset) {
        return productDao.readActiveProductsByCategory(category.getId(), limit, offset);
    }

    @Override
    @Deprecated
    public List<Product> findActiveProductsByCategory(Category category, Date currentDate) {
        return productDao.readActiveProductsByCategory(category.getId(), currentDate);
    }
    
    @Override
    @Deprecated
    public List<Product> findFilteredActiveProductsByCategory(Category category, Date currentDate, SearchCriteria searchCriteria) {
        return productDao.readFilteredActiveProductsByCategory(category.getId(), currentDate, searchCriteria);
    }
    
    @Override
    @Deprecated
    public List<Product> findFilteredActiveProductsByQuery(String query, Date currentDate, SearchCriteria searchCriteria) {
        return productDao.readFilteredActiveProductsByQuery(query, currentDate, searchCriteria);
    }

    @Override
    @Deprecated
    public List<Product> findActiveProductsByCategory(Category category, Date currentDate, int limit, int offset) {
        return productDao.readActiveProductsByCategory(category.getId(), currentDate, limit, offset);
    }

    @Override
    public List<ProductBundle> findAutomaticProductBundles() {
        List<ProductBundle> bundles =  productDao.readAutomaticProductBundles();
        Collections.sort(bundles, new ProductBundleComparator());
        return bundles;
    }

    @Override
    @Transactional("blTransactionManager")
    public Product saveProduct(Product product) {
        return productDao.save(product);
    }

    @Override
    public Category findCategoryById(Long categoryId) {
        return categoryDao.readCategoryById(categoryId);
    }

    @Override
    @Deprecated
    public Category findCategoryByName(String categoryName) {
        return categoryDao.readCategoryByName(categoryName);
    }

    @Override
    public List<Category> findCategoriesByName(String categoryName) {
        return categoryDao.readCategoriesByName(categoryName);
    }

    @Override
    public List<Category> findCategoriesByName(String categoryName, int limit, int offset) {
        return categoryDao.readCategoriesByName(categoryName, limit, offset);
    }

    @Override
    @Transactional("blTransactionManager")
    public Category saveCategory(Category category) {
        return categoryDao.save(category);
    }
    
    @Override
    @Transactional("blTransactionManager")
    public void removeCategory(Category category){
        categoryDao.delete(category);
    }

    @Override
    @Transactional("blTransactionManager")
    public void removeProduct(Product product) {
        productDao.delete(product);
    }

    @Override
    public List<Category> findAllCategories() {
        return categoryDao.readAllCategories();
    }

    @Override
    public List<Category> findAllCategories(int limit, int offset) {
        return categoryDao.readAllCategories(limit, offset);
    }
    
    @Override
    public List<Category> findAllParentCategories() {
        return categoryDao.readAllParentCategories();
    }

    @Override
    public List<Category> findAllSubCategories(Category category) {
        return categoryDao.readAllSubCategories(category);
    }

    @Override
    public List<Category> findAllSubCategories(Category category, int limit, int offset) {
        return categoryDao.readAllSubCategories(category, limit, offset);
    }

    @Override
    public List<Category> findActiveSubCategoriesByCategory(Category category) {
        return categoryDao.readActiveSubCategoriesByCategory(category);
    }

    @Override
    public List<Category> findActiveSubCategoriesByCategory(Category category, int limit, int offset) {
        return categoryDao.readActiveSubCategoriesByCategory(category, limit, offset);
    }

    @Override
    public List<Product> findAllProducts() {
        return categoryDao.readAllProducts();
    }

    @Override
    public List<Product> findAllProducts(int limit, int offset) {
        return categoryDao.readAllProducts(limit, offset);
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
    @Transactional("blTransactionManager")
    public Sku saveSku(Sku sku) {
        return skuDao.save(sku);
    }
    
    @Override
    @Transactional("blTransactionManager")
    public SkuFee saveSkuFee(SkuFee fee) {
        return skuDao.saveSkuFee(fee);
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

    @Override
    public List<Product> findProductsForCategory(Category category) {
        return productDao.readProductsByCategory(category.getId());
    }

    @Override
    public List<Product> findProductsForCategory(Category category, int limit, int offset) {
        return productDao.readProductsByCategory(category.getId(), limit, offset);
    }

    public void setCategoryDao(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    @Deprecated
    public Map<String, List<Long>> getChildCategoryURLMapByCategoryId(Long categoryId) {
        Category category = findCategoryById(categoryId);
        if (category != null) {
            return category.getChildCategoryURLMap();
        }
        return null;
    }
    
    @Override
    public Category createCategory() {
        return categoryDao.create();
    }
    
    @Override
    public Sku createSku() {
        return skuDao.create();
    }
    
    @Override
    public Product createProduct(ProductType productType) {
        return productDao.create(productType);
    }

    @Override
    public List<ProductOption> readAllProductOptions() {
        return productOptionDao.readAllProductOptions();
    }
    
    @Override
    @Transactional("blTransactionManager")
    public ProductOption saveProductOption(ProductOption option) {
        return productOptionDao.saveProductOption(option);
    }
    
    @Override
    public ProductOption findProductOptionById(Long productOptionId) {
        return productOptionDao.readProductOptionById(productOptionId);
    }

    @Override
    public ProductOptionValue findProductOptionValueById(Long productOptionValueId) {
        return productOptionDao.readProductOptionValueById(productOptionValueId);
    }

    @Override
    public Category findCategoryByURI(String uri) {
        if (extensionManager != null) {
            ExtensionResultHolder holder = new ExtensionResultHolder();
            ExtensionResultStatusType result = extensionManager.getProxy().findCategoryByURI(uri, holder);
            if (ExtensionResultStatusType.HANDLED.equals(result)) {
                return (Category) holder.getResult();
            }
        }
        return categoryDao.findCategoryByURI(uri);
    }

    @Override
    public Product findProductByURI(String uri) {
        if (extensionManager != null) {
            ExtensionResultHolder holder = new ExtensionResultHolder();
            ExtensionResultStatusType result = extensionManager.getProxy().findProductByURI(uri, holder);
            if (ExtensionResultStatusType.HANDLED.equals(result)) {
                return (Product) holder.getResult();
            }
        }
        List<Product> products = productDao.findProductByURI(uri);
        if (products == null || products.size() == 0) {
            return null;
        } else if (products.size() == 1) {
            return products.get(0);
        } else {
            // First check for a direct hit on the url
            for(Product product : products) {
                if (uri.equals(product.getUrl())) {
                    return product;
                }
            }
            
            for(Product product : products) {
                // Next check for a direct hit on the generated URL.
                if (uri.equals(product.getGeneratedUrl())) {
                    return product;
                }
            }
            
            // Otherwise, return the first product
            return products.get(0);
        }
    }
    
}
