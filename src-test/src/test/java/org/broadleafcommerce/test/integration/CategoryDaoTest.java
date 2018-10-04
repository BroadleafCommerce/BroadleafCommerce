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
package org.broadleafcommerce.test.integration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.CategoryDao;
import org.broadleafcommerce.catalog.dao.ProductDao;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.FeaturedProduct;
import org.broadleafcommerce.catalog.domain.FeaturedProductImpl;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.broadleafcommerce.test.dataprovider.CategoryDaoDataProvider;
import org.testng.annotations.Test;

public class CategoryDaoTest extends BaseTest {

    @Resource
    private CategoryDao categoryDao;
    @Resource
    private ProductDao productDao;

    @Test(groups =  {"testSetFeaturedProducts"}, dataProvider="basicCategory", dataProviderClass=CategoryDaoDataProvider.class)
    public void testSetFeaturedProducts(Category category) {
        category = categoryDao.save(category);

        Product product = new ProductImpl();
        product.setModel("KGX200");
        product.setDescription("This thing will change your life");
        product.setName("Test Product");
        product = productDao.save(product);

        FeaturedProduct featuredProduct = new FeaturedProductImpl();
        featuredProduct.setCategory(category);
        featuredProduct.setProduct(product);
        featuredProduct.setPromotionMessage("BUY ME NOW!!!!");
        List<FeaturedProduct> featuredProducts = new ArrayList<FeaturedProduct>();
        featuredProducts.add(featuredProduct);
        category.setFeaturedProducts(featuredProducts);
        category = categoryDao.save(category);

        Category categoryTest = categoryDao.readCategoryById(category.getId());
        FeaturedProduct featuredProductTest = categoryTest.getFeaturedProducts().get(0);

        assert (featuredProductTest.getPromotionMessage() == "BUY ME NOW!!!!");
        assert (featuredProductTest.getProduct().getModel().equals("KGX200"));
    }

}
