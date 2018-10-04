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

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.SkuDao;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.test.dataprovider.SkuDaoDataProvider;
import org.broadleafcommerce.util.money.Money;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class SkuDaoTest extends BaseTest {

    private Long skuId;

    @Resource
    private SkuDao skuDao;

    @Test(groups = { "createSku" }, dataProvider = "basicSku", dataProviderClass = SkuDaoDataProvider.class, dependsOnGroups = { "readCustomer1", "createOrder", "createProducts" })
    @Rollback(false)
    public void createSku(Sku sku) {
        sku.setSalePrice(new Money(BigDecimal.valueOf(10.0)));
        sku.setRetailPrice(new Money(BigDecimal.valueOf(15.0)));
        sku.setName("test sku");
        assert sku.getId() == null;
        sku = skuDao.save(sku);
        assert sku.getId() != null;
        skuId = sku.getId();
    }

    @Test(groups = { "readFirstSku" }, dependsOnGroups = { "createSku" })
    public void readFirstSku() {
        Sku si = skuDao.readFirstSku();
        assert si != null;
        assert si.getId() != null;
    }

    @Test(groups = { "readSkuById" }, dependsOnGroups = { "createSku" })
    public void readSkuById() {
        Sku item = skuDao.readSkuById(skuId);
        assert item != null;
        assert item.getId() == skuId;
    }
}
