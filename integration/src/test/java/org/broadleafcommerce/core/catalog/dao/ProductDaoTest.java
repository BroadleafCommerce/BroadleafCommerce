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

package org.broadleafcommerce.core.catalog.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.common.util.WeightUnitOfMeasureType;
import org.broadleafcommerce.core.catalog.ProductDataProvider;
import org.broadleafcommerce.core.catalog.domain.CrossSaleProductImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Weight;
import org.broadleafcommerce.core.catalog.domain.RelatedProduct;
import org.broadleafcommerce.core.catalog.domain.UpSaleProductImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

public class ProductDaoTest extends BaseTest {

    @Resource
    private ProductDao productDao;
    
    @Resource
    private CatalogService catalogService;

    @Test(groups="createProducts", dataProvider="setupProducts", dataProviderClass=ProductDataProvider.class)
    @Rollback(false)
    public void createProducts(Product product){
        product = catalogService.saveProduct(product);
        assert(product.getId() != null);
    }

    @Test(groups="createUpSaleValues", dataProvider="basicUpSaleValue", dataProviderClass=ProductDataProvider.class, dependsOnGroups="createProducts")
    @Rollback(false)
    public void createUpSaleValues(Product product){
        product = catalogService.saveProduct(product);
        assert(product.getId() != null);
    }

    @Test(groups="testReadProductsWithUpSaleValues", dataProvider="basicUpSaleValue", dataProviderClass=ProductDataProvider.class, dependsOnGroups="createUpSaleValues")
    @Transactional
    public void testReadProductsWithUpSaleValues(Product product) {
        Product result = productDao.readProductById(product.getId());
        List<RelatedProduct> related = result.getUpSaleProducts();

        assert(related != null);
        assert(!related.isEmpty());
        assert(related.size() == 2 || related.size() == 3);

        for(RelatedProduct rp : related){
            assert(rp instanceof UpSaleProductImpl);
        }
    }

    @Test(groups="createCrossSaleValues", dataProvider="basicCrossSaleValue", dataProviderClass=ProductDataProvider.class, dependsOnGroups="testReadProductsWithUpSaleValues")
    @Rollback(false)
    public void createCrossSaleValues(Product product){
        product = catalogService.saveProduct(product);
        assert(product.getId() != null);
    }

    @Test(groups="testReadProductsWithCrossSaleValues", dataProvider="basicCrossSaleValue", dataProviderClass=ProductDataProvider.class, dependsOnGroups="createCrossSaleValues")
    @Transactional
    public void testReadProductsWithCrossSaleValues(Product product) {
        Product result = productDao.readProductById(product.getId());
        List<RelatedProduct> related = result.getCrossSaleProducts();

        assert(related != null);
        assert(!related.isEmpty());
        assert(related.size() == 2 || related.size() == 3);

        for(RelatedProduct rp : related){
            assert(rp instanceof CrossSaleProductImpl);
        }
    }

    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    public void testReadProductsById(Product product) {
        product = catalogService.saveProduct(product);
        Product result = productDao.readProductById(product.getId());
        assert product.equals(result);
    }

    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    @Transactional
    public void testReadProductsByName(Product product) {
        String name = product.getName();
        product = catalogService.saveProduct(product);
        List<Product> result = productDao.readProductsByName(name);
        assert result.contains(product);
    }

    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    public void testFeaturedProduct(Product product) {
        product = catalogService.saveProduct(product);
        Long productId = product.getId();
        product.setFeaturedProduct(true);
        catalogService.saveProduct(product);
        Product testProduct = productDao.readProductById(productId);
        assert (testProduct.isFeaturedProduct() == true);
    }
    
}
