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
package org.broadleafcommerce.catalog.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.ProductDao;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.CategoryImpl;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.domain.SkuImpl;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.util.money.Money;
import org.testng.annotations.Test;

public class CatalogTest extends BaseTest {

    @Resource
    private CatalogService catalogService;
    @Resource
    private ProductDao productDao;

    @Test
    public void testCatalog() throws Exception {
        Category category = new CategoryImpl();
        category.setName("Soaps");
        category = catalogService.saveCategory(category);
        Product newProduct = new ProductImpl();

        Calendar activeStartCal = Calendar.getInstance();
        activeStartCal.add(Calendar.DAY_OF_YEAR, -2);
        newProduct.setActiveStartDate(activeStartCal.getTime());

        newProduct.setDefaultCategory(category);
        newProduct.setName("Lavender Soap");
        newProduct = catalogService.saveProduct(newProduct);
        Long newProductId = newProduct.getId();

        Product testProduct = catalogService.findProductById(newProductId);
        assert testProduct.getId().equals(testProduct.getId());

        Category testCategory = catalogService.findCategoryByName("Soaps");
        assert testCategory.getId().equals(category.getId());

        testCategory = catalogService.findCategoryById(category.getId());
        assert testCategory.getId().equals(category.getId());

        List<Category> categories = catalogService.findAllCategories();
        assert categories != null && categories.size() == 1;

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
        List<Sku> allSkus = new ArrayList<Sku>();
        allSkus.add(newSku);
        newProduct.setAllSkus(allSkus);
        newSku = catalogService.saveSku(newSku);
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

        foundProduct = false;
        List<Product> findProducts = productDao.readProductsBySku(newProduct.getSkus().get(0).getId());
        for (Product findProduct : findProducts) {
            if (findProduct.getId().equals(newProduct.getId())) {
                foundProduct = true;
            }
        }

        assert foundProduct == true;

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

        assert sku.isDiscountable() == null;
        sku.setDiscountable(null);
        assert sku.isDiscountable() == null;
        sku.setDiscountable(true);
        assert sku.isDiscountable() == true;
        sku.setDiscountable(false);
        assert sku.isDiscountable() == false;

        assert sku.isAvailable() == null;
        sku.setAvailable(null);
        assert sku.isAvailable() == null;
        sku.setAvailable(true);
        assert sku.isAvailable() == true;
        sku.setAvailable(false);
        assert sku.isAvailable() == false;

        assert sku.getName() == null;


    }
}
