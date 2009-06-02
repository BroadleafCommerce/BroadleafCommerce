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

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.ProductDao;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.test.dataprovider.ProductDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class ProductDaoTest extends BaseTest {

    @Resource
    private ProductDao productDao;

    @Test(groups={"createProduct"},dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    @Rollback(false)
    public void testMaintainProduct(Product product) {
        assert product.getId() == null;
        product = productDao.save(product);
        assert product.getId() != null;
    }

    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    public void testReadProductsById(Product product) {
        product = productDao.save(product);
        Product result = productDao.readProductById(product.getId());
        assert product.equals(result);
    }

    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    public void testReadProductsByName(Product product) {
        String name = product.getName();
        product = productDao.save(product);
        List<Product> result = productDao.readProductsByName(name);
        assert result.contains(product);
    }

}
