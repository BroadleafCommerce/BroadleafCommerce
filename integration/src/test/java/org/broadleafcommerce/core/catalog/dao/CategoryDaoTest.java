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

package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.core.catalog.CategoryDaoDataProvider;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.FeaturedProduct;
import org.broadleafcommerce.core.catalog.domain.FeaturedProductImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoTest extends BaseTest {

    @Resource
    private CategoryDao categoryDao;
    @Resource
    private CatalogService catalogService;

    @Test(groups =  {"testSetFeaturedProducts"}, dataProvider="basicCategory", dataProviderClass=CategoryDaoDataProvider.class)
    @Transactional
    public void testSetFeaturedProducts(Category category) {
        category = catalogService.saveCategory(category);

        Sku sku = new SkuImpl();
        sku.setDescription("This thing will change your life");
        sku.setName("Test Product");
        catalogService.saveSku(sku);
        
        Product product = new ProductImpl();
        product.setModel("KGX200");
        product.setDefaultSku(sku);
        product = catalogService.saveProduct(product);

        FeaturedProduct featuredProduct = new FeaturedProductImpl();
        featuredProduct.setCategory(category);
        featuredProduct.setProduct(product);
        featuredProduct.setPromotionMessage("BUY ME NOW!!!!");
        List<FeaturedProduct> featuredProducts = new ArrayList<FeaturedProduct>();
        featuredProducts.add(featuredProduct);
        category.setFeaturedProducts(featuredProducts);
        category = catalogService.saveCategory(category);

        Category categoryTest = categoryDao.readCategoryById(category.getId());
        FeaturedProduct featuredProductTest = categoryTest.getFeaturedProducts().get(0);

        assert (featuredProductTest.getPromotionMessage() == "BUY ME NOW!!!!");
        assert (featuredProductTest.getProduct().getModel().equals("KGX200"));
    }

}
