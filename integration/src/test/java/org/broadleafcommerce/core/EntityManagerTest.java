/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.broadleafcommerce.core.catalog.ProductDataProvider;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EntityManagerTest extends BaseTest {

    @PersistenceContext(unitName="blPU")
    private EntityManager entityManager;

    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    @Transactional
    public void testDeleteProductWithEntityManager(Product product) {
        product = entityManager.merge(product);
        Product result = entityManager.find(ProductImpl.class,product.getId());
        Assert.assertEquals(product, result);
        entityManager.remove(result);
        entityManager.flush();
        Assert.assertNull(entityManager.find(ProductImpl.class,product.getId()));
    }

}