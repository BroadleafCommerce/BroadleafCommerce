#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package}.${artifactId}.common.${artifactId};

import ${package}.${artifactId}.common.BaseTest;
import org.broadleafcommerce.core.catalog.CategoryDaoDataProvider;
import org.broadleafcommerce.core.catalog.dao.CategoryDao;
import org.broadleafcommerce.core.catalog.domain.*;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.springframework.transaction.annotation.Transactional;
import org.${artifactId}ng.annotations.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoTest extends BaseTest {

    @Resource
    private CategoryDao categoryDao;
    @Resource
    private CatalogService catalogService;

    @Test(groups =  {"${artifactId}SetFeaturedProducts"}, dataProvider="basicCategory", dataProviderClass=CategoryDaoDataProvider.class)
    @Transactional
    public void ${artifactId}SetFeaturedProducts(Category category) {
        category = catalogService.saveCategory(category);

        Product product = new ProductImpl();
        product.setModel("KGX200");
        product.setDescription("This thing will change your life");
        product.setName("Test Product");
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
