/*
 * #%L
 * BroadleafCommerce Integration
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

import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.media.domain.MediaImpl;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXref;
import org.broadleafcommerce.core.catalog.domain.CategoryProductXrefImpl;
import org.broadleafcommerce.core.catalog.domain.CategoryXref;
import org.broadleafcommerce.core.catalog.domain.CategoryXrefImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@SuppressWarnings("deprecation")
public class CatalogTest extends BaseTest {

    @Resource
    private CatalogService catalogService;

    @Test(groups = {"testCatalog"})
    @Transactional
    public void testCatalog() throws Exception {
        Category category = new CategoryImpl();
        category.setName("Soaps");
        category = catalogService.saveCategory(category);
        Category category2 = new CategoryImpl();
        category2.setName("Towels");
        category2 = catalogService.saveCategory(category2);
        Category category3 = new CategoryImpl();
        category3.setName("SuperCategory");
        category3 = catalogService.saveCategory(category3);

        CategoryXref temp = new CategoryXrefImpl();
        temp.setCategory(category);
        temp.setSubCategory(category3);
        category3.getAllParentCategoryXrefs().add(temp);
        category3 = catalogService.saveCategory(category3);
        
        // Test category hierarchy
        Long cat3Id = category3.getId();
        category3 = null;
        category3 = catalogService.findCategoryById(cat3Id);
        category3.getAllParentCategoryXrefs().clear();
        CategoryXref temp2 = new CategoryXrefImpl();
        temp2.setCategory(category);
        temp2.setSubCategory(category3);
        category3.getAllParentCategoryXrefs().add(temp2);
        CategoryXref temp3 = new CategoryXrefImpl();
        temp3.setCategory(category2);
        temp3.setSubCategory(category3);
        category3.getAllParentCategoryXrefs().add(temp3);
        category3 = catalogService.saveCategory(category3);
        assert category3.getAllParentCategoryXrefs().size() == 2;
        
        Product newProduct = new ProductImpl();
        Sku newDefaultSku = new SkuImpl();
        newDefaultSku = catalogService.saveSku(newDefaultSku);
        newProduct.setDefaultSku(newDefaultSku);
        newProduct.setName("Lavender Soap");

        Calendar activeStartCal = Calendar.getInstance();
        activeStartCal.add(Calendar.DAY_OF_YEAR, -2);
        newProduct.setActiveStartDate(activeStartCal.getTime());
//        newProduct.setAllParentCategories(allParentCategories);
        newProduct.setDefaultCategory(category);
        newProduct.getAllParentCategoryXrefs().clear();
        newProduct = catalogService.saveProduct(newProduct);

        CategoryProductXref categoryXref = new CategoryProductXrefImpl();
        categoryXref.setProduct(newProduct);
        categoryXref.setCategory(category);
        newProduct.getAllParentCategoryXrefs().add(categoryXref);

        CategoryProductXref categoryXref2 = new CategoryProductXrefImpl();
        categoryXref2.setProduct(newProduct);
        categoryXref2.setCategory(category2);
        newProduct.getAllParentCategoryXrefs().add(categoryXref2);
        newProduct = catalogService.saveProduct(newProduct);

        Long newProductId = newProduct.getId();

        Product testProduct = catalogService.findProductById(newProductId);
        assert testProduct.getId().equals(testProduct.getId());

        Category testCategory = catalogService.findCategoryByName("Soaps");
        assert testCategory.getId().equals(category.getId());

        testCategory = catalogService.findCategoryById(category.getId());
        assert testCategory.getId().equals(category.getId());
                
        Map<String, Media> categoryMedia = testCategory.getCategoryMedia();
        Media media = new MediaImpl();
        media.setAltText("test");
        media.setTitle("large");
        media.setUrl("http://myUrl");
        categoryMedia.put("large", media);
        catalogService.saveCategory(testCategory);

        testCategory = catalogService.findCategoryById(category.getId());
        assert(testCategory.getCategoryMedia().get("large") != null);

        List<Category> categories = catalogService.findAllCategories();
        assert categories != null && categories.size() == 3;

        List<Product> products = catalogService.findAllProducts();
        boolean foundProduct = false;

        for (Product product : products ) {
            if (product.getId().equals(newProductId)) {
                foundProduct = true;
            }
        }
        assert foundProduct == true;


        products = catalogService.findProductsByName(newProduct.getName());
        foundProduct = false;

        for (Product product : products ) {
            if (product.getId().equals(newProductId)) {
                foundProduct = true;
            }
        }
        assert foundProduct == true;


        Sku newSku = new SkuImpl();
        newSku.setName("Under Armor T-Shirt -- Red");
        newSku.setRetailPrice(new Money(14.99));
        newSku.setActiveStartDate(activeStartCal.getTime());
        newSku = catalogService.saveSku(newSku);
        List<Sku> allSkus = new ArrayList<Sku>();
        allSkus.add(newSku);
        newProduct.setAdditionalSkus(allSkus);
        newProduct = catalogService.saveProduct(newProduct);
        Long skuId = newProduct.getSkus().get(0).getId();

        Sku testSku = catalogService.findSkuById(skuId);
        assert testSku.getId().equals(skuId);

        List<Sku> testSkus = catalogService.findAllSkus();
        boolean foundSku = false;

        for (Sku sku : testSkus) {
            if (sku.getId().equals(skuId)) {
                foundSku = true;
            }
        }

        assert foundSku == true;

        List<Long> skuIds = new ArrayList<Long>();
        skuIds.add(skuId);
        testSkus = catalogService.findSkusByIds(skuIds);
        foundSku = false;

        for (Sku sku : testSkus) {
            if (sku.getId().equals(skuId)) {
                foundSku = true;
            }
        }

        assert foundSku == true;

    }

    @Test
    public void testSkus() throws Exception {
        Sku sku = new SkuImpl();
        String longDescription = "This is a great product that will help the Longhorns win.";
        String description = "This is a great product.";
        sku.setLongDescription(longDescription);
        assert sku.getLongDescription().equals(longDescription);
        sku.setDescription(description);
        assert sku.getDescription().equals(description);

        assert sku.isTaxable() == null;
        sku.setTaxable(null);
        assert sku.isTaxable() == null;
        sku.setTaxable(true);
        assert sku.isTaxable() == true;
        sku.setTaxable(false);
        assert sku.isTaxable() == false;

        sku.setDiscountable(null);
        assert sku.isDiscountable() == false;
        sku.setDiscountable(true);
        assert sku.isDiscountable() == true;
        sku.setDiscountable(false);
        assert sku.isDiscountable() == false;

        assert sku.isAvailable() == true;
        sku.setAvailable(null);
        assert sku.isAvailable() == true;
        sku.setAvailable(true);
        assert sku.isAvailable() == true;
        sku.setAvailable(false);
        assert sku.isAvailable() == false;

        assert sku.getName() == null;


    }
}
