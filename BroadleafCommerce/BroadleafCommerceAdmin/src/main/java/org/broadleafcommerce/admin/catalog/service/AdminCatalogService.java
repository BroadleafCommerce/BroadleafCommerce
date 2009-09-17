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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.apache.commons.lang.ObjectUtils;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.CategoryImpl;
import org.broadleafcommerce.catalog.domain.CrossSaleProductImpl;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.RelatedProduct;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.media.domain.Media;
import org.broadleafcommerce.media.domain.MediaImpl;
import org.springframework.stereotype.Service;

import flex.messaging.io.amf.ASObject;

@Service("blAdminCatalogService")
public class AdminCatalogService {
    
    @Resource(name = "blCatalogService")
    CatalogService catalogService;
    
    public Category findCategoryById(Long categoryId) {
    	return catalogService.findCategoryById(categoryId);
    }
    
    public Category findCategoryByName(String categoryName) {
    	return catalogService.findCategoryByName(categoryName);
    }
    
    public Product findProductById(Long productId) {
        return catalogService.findProductById(productId);
    }

    public List<Product> findProductsByName(String searchName) {
        return catalogService.findProductsByName(searchName);
    }

    public List<Product> findProductsByCategory(Category category) {
        return catalogService.findProductsForCategory(category); 
    }

    public Product saveProduct(Product product) {
    	Product newProduct = catalogService.findProductById(product.getId());
    	List<Sku> skus = new ArrayList<Sku>(product.getAllSkus().size());
    	skus.addAll(product.getAllSkus());
    	product.getAllSkus().clear();
    	for (int i=0; i< skus.size(); i++){
    		product.getAllSkus().add(catalogService.saveSku(skus.get(i)));
    	}
        return catalogService.saveProduct(mendProductTrees(product));
    }
    
    public Category saveCategory(Category category) {
        Category cat = catalogService.saveCategory(mendCategoryTrees(category));       
        return cat; 
    }

    public List<Category> findAllCategories() {
    	List<Category> categories = catalogService.findAllCategories();
    	categories.get(0).getAllChildCategories().size();
    	return categories;
    }

    public List<Product> findAllProducts() {
        return catalogService.findAllProducts();
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
    
}
