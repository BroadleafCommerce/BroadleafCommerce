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
package org.broadleafcommerce.admin.catalog.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;


import org.broadleafcommerce.catalog.dao.CategoryDao;
import org.broadleafcommerce.catalog.dao.CategoryXrefDao;
import org.broadleafcommerce.catalog.dao.ProductDao;
import org.broadleafcommerce.catalog.dao.SkuDao;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.CategoryXref;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.springframework.stereotype.Service;

@Service("blAdminCatalogService")
public class AdminCatalogService {
    
    @Resource(name = "blCatalogService")
    CatalogService catalogService;
    
    @Resource(name="blCategoryDao")
    protected CategoryDao categoryDao;

    @Resource(name="blCategoryXrefDao")
    protected CategoryXrefDao categoryXrefDao;

    @Resource(name="blProductDao")
    protected ProductDao productDao;

    @Resource(name="blSkuDao")
    protected SkuDao skuDao;

    
    public Category findCategoryById(Long categoryId) {
        return catalogService.findCategoryById(categoryId);
    }
    
    public Category findCategoryByName(String categoryName) {
        return catalogService.findCategoryByName(categoryName);
    }
    
    public Product findProductById(Long productId) {
        Product product = catalogService.findProductById(productId);
        product.getUpSaleProducts().size();
        product.getCrossSaleProducts().size();
        return product;
    }

    public List<Product> findProductsByName(String searchName) {
        return catalogService.findProductsByName(searchName);
    }

    public List<Product> findProductsByCategory(Category category) {
        List<Product> products = catalogService.findProductsForCategory(category); 
        if(products.size() > 0){
            for(Product product : products){
                product.getCrossSaleProducts().size();
                product.getUpSaleProducts().size();
            }
        }       
        return products;
    }

    public Product saveProduct(Product product) {
        List<Sku> skus = new ArrayList<Sku>(product.getAllSkus().size());
        skus.addAll(product.getAllSkus());
        product.getAllSkus().clear();
        for (int i=0; i< skus.size(); i++){
            product.getAllSkus().add(catalogService.saveSku(skus.get(i)));
        }
        return catalogService.saveProduct(mendProductTrees(product));
    }
    
    public void deleteProduct(Product product){
        product.getAllParentCategories().clear();
        catalogService.saveProduct(product);
        productDao.delete(product);
    }

    public Category updateCategoryParents(Category category, Category oldParent, Category newParent){
        Category updatedOldParent = null;
        if(oldParent != null){
            updatedOldParent = catalogService.saveCategory(mendCategoryTrees(oldParent));           
        }
        Category updatedNewParent = catalogService.saveCategory(mendCategoryTrees(newParent));
        Category updatedCategory = catalogService.saveCategory(mendCategoryTrees(category));
        if(updatedOldParent != null){
            saveCategoryDisplayOrders(updatedOldParent);
        }
        saveCategoryDisplayOrders(updatedNewParent);
        return updatedCategory;
    }
    
    public Category saveCategory(Category category) {
        Category cat = catalogService.saveCategory(mendCategoryTrees(category));
        int index = 0;
        for(Category parentCategory : cat.getAllParentCategories()){
            parentCategory.getAllChildCategories().size();
            if(!parentCategory.getAllChildCategories().contains(cat)){
                parentCategory.getAllChildCategories().add(index,cat);
                catalogService.saveCategory(parentCategory);
                saveCategoryDisplayOrders(parentCategory);
            }
            index++;
        }
        if(cat.getDefaultParentCategory() != null && cat.getAllParentCategories().indexOf(cat.getDefaultParentCategory()) < 0){
//          cat.getAllParentCategories().add(cat.getDefaultParentCategory());
            cat.getDefaultParentCategory().getAllChildCategories().add(cat);
            catalogService.saveCategory(cat.getDefaultParentCategory());
        }
        return cat; 
    }
    
    public void deleteCategory(Category category, Category parentCategory){
        parentCategory = mendCategoryTrees(parentCategory);
        parentCategory.getAllChildCategories().size();
        parentCategory.getAllChildCategories().remove(category);
        categoryDao.save(parentCategory);
        category = mendCategoryTrees(category);  
        catalogService.removeCategory(category);
    }

    public List<Category> findAllCategories() {
        List<Category> categories = catalogService.findAllCategories();
        if(categories.size() > 0){
            for(Category category : categories){
                category.getAllChildCategories().size();
                category.getFeaturedProducts().size();
            }
        }
        return categories;
    }

    public List<Product> findAllProducts() {
        List<Product> products = catalogService.findAllProducts();
        if(products.size() > 0){
            for(Product product : products){
                product.getCrossSaleProducts().size();
                product.getUpSaleProducts().size();
            }
        }
        return products;
    }

    public List<Sku> findAllSkus() {
        return catalogService.findAllSkus();
    }

    public Sku findSkuById(Long skuId) {
        return catalogService.findSkuById(skuId);
    }

    public Sku saveSku(Sku sku) {
        return catalogService.saveSku(sku);
    }
    
    public void deleteSku(Sku sku){
        skuDao.delete(sku);
    }

    public List<Sku> findSkusByIds(List<Long> ids) {
        return catalogService.findSkusByIds(ids);
    }

    private Category mendCategoryTrees(Category category){
        addAllCategories(category.getAllChildCategories(), mendCategoriesList(category.getAllChildCategories()));
        addAllCategories(category.getAllParentCategories(), mendCategoriesList(category.getAllParentCategories()));
        if(category.getDefaultParentCategory() != null){            
            category.setDefaultParentCategory(catalogService.findCategoryById(category.getDefaultParentCategory().getId()));
        }
        return category;
    }
    
    private Product mendProductTrees(Product product){
        addAllCategories(product.getAllParentCategories(), mendCategoriesList(product.getAllParentCategories()));
        product.setDefaultCategory(mendCategoryTrees(product.getDefaultCategory()));
        return product;
    }
    
    private List<Category> mendCategoriesList(List<Category> categories){
        List<Category> mendedCategories = new ArrayList<Category>();
        for(Category category : categories){
            mendedCategories.add(catalogService.findCategoryById(category.getId()));
        }
        return mendedCategories;
    }
    
    private void addAllCategories(List<Category> categories, List<Category> newCategories){
        categories.clear();
        for(Category category : newCategories){
            categories.add(category);
        }
    }
    
    private void saveCategoryDisplayOrders(Category category){
        int index = 0;
        for(Category childCategory : category.getAllChildCategories()){
            CategoryXref categoryXref = categoryXrefDao.readXrefByIds(category.getId(), childCategory.getId());
            categoryXref.setDisplayOrder(new Long(index));
            index++;
            categoryXrefDao.save(categoryXref);
        }
        
    }
    
}
