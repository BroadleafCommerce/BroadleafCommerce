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
package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.core.catalog.ProductDataProvider;
import org.broadleafcommerce.core.catalog.domain.CrossSaleProductImpl;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.RelatedProduct;
import org.broadleafcommerce.core.catalog.domain.UpSaleProductImpl;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoTest extends BaseTest {

    @Resource
    private ProductDao productDao;
    
    @Resource
    private CatalogService catalogService;

    private List<Product> savedProducts = new ArrayList<Product>();

    private static RelatedProduct getRelatedUpSaleProduct(Product prod, Product prodToRelate, List<RelatedProduct> upSales){
        RelatedProduct rp1 = new UpSaleProductImpl();
        rp1.setProduct(prod);
        rp1.setPromotionMessage("brand new coffee");
        rp1.setRelatedProduct(prodToRelate);

        upSales.add(rp1);
        return rp1;
    }

    private static RelatedProduct getRelatedCrossProduct(Product prod, Product prodToRelate, List<RelatedProduct> upSales){
        RelatedProduct rp1 = new CrossSaleProductImpl();
        rp1.setProduct(prod);
        rp1.setPromotionMessage("brand new coffee");
        rp1.setRelatedProduct(prodToRelate);

        upSales.add(rp1);
        return rp1;
    }

    @Test(groups="createProducts", dataProvider="setupProducts", dataProviderClass=ProductDataProvider.class)
    @Rollback(false)
    @Transactional
    public void createProducts(Product product) {
        product = catalogService.saveProduct(product);
        assert(product.getId() != null);
        savedProducts.add(product);
    }

    @Test(groups="createUpSaleValues", dependsOnGroups="createProducts")
    @Rollback(false)
    @Transactional
    public void createUpSaleValues(){
        Product prod1 = savedProducts.get(0);
        List<RelatedProduct> upSales = new ArrayList<RelatedProduct>();
        getRelatedUpSaleProduct(prod1, savedProducts.get(2), upSales);
        getRelatedUpSaleProduct(prod1, savedProducts.get(3), upSales);
        getRelatedUpSaleProduct(prod1, savedProducts.get(4), upSales);
        prod1.setUpSaleProducts(upSales);
        prod1 = catalogService.saveProduct(prod1);
        assert(prod1.getId() != null);

        Product prod2 = savedProducts.get(1);
        List<RelatedProduct> upSales2 = new ArrayList<RelatedProduct>();
        getRelatedUpSaleProduct(prod2, savedProducts.get(5), upSales2);
        getRelatedUpSaleProduct(prod2, savedProducts.get(6), upSales2);
        prod2.setUpSaleProducts(upSales2);
        prod2 = catalogService.saveProduct(prod2);
        assert(prod2.getId() != null);
    }

    @Test(groups="testReadProductsWithUpSaleValues", dependsOnGroups="createUpSaleValues")
    @Transactional
    public void testReadProductsWithUpSaleValues() {
        Product result = productDao.readProductById(savedProducts.get(0).getId());
        List<RelatedProduct> related = result.getUpSaleProducts();

        assert(related != null);
        assert(!related.isEmpty());
        assert(related.size() == 2 || related.size() == 3);

        for(RelatedProduct rp : related){
            assert(rp instanceof UpSaleProductImpl);
        }
    }

    @Test(groups="createCrossSaleValues", dependsOnGroups="testReadProductsWithUpSaleValues")
    @Rollback(false)
    @Transactional
    public void createCrossSaleValues(){
        Product prod1 = savedProducts.get(0);
        List<RelatedProduct> crossSale = new ArrayList<RelatedProduct>();
        getRelatedCrossProduct(prod1, savedProducts.get(2), crossSale);
        getRelatedCrossProduct(prod1, savedProducts.get(3), crossSale);
        getRelatedCrossProduct(prod1, savedProducts.get(4), crossSale);
        prod1.setCrossSaleProducts(crossSale);
        prod1 = catalogService.saveProduct(prod1);
        assert(prod1.getId() != null);

        Product prod2 = savedProducts.get(1);
        List<RelatedProduct> crossSale2 = new ArrayList<RelatedProduct>();
        getRelatedCrossProduct(prod2, savedProducts.get(5), crossSale2);
        getRelatedCrossProduct(prod2, savedProducts.get(6), crossSale2);
        prod2.setCrossSaleProducts(crossSale2);
        prod2 = catalogService.saveProduct(prod2);
        assert(prod2.getId() != null);
    }

    @Test(groups="testReadProductsWithCrossSaleValues", dependsOnGroups="createCrossSaleValues")
    @Transactional
    public void testReadProductsWithCrossSaleValues() {
        Product result = productDao.readProductById(savedProducts.get(1).getId());
        List<RelatedProduct> related = result.getCrossSaleProducts();

        assert(related != null);
        assert(!related.isEmpty());
        assert(related.size() == 2 || related.size() == 3);

        for(RelatedProduct rp : related){
            assert(rp instanceof CrossSaleProductImpl);
        }
    }

    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    @Transactional
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
    @Transactional
    public void testFeaturedProduct(Product product) {
        product = catalogService.saveProduct(product);
        Long productId = product.getId();
        product.setFeaturedProduct(true);
        catalogService.saveProduct(product);
        Product testProduct = productDao.readProductById(productId);
        assert (testProduct.isFeaturedProduct() == true);
    }
    
}
