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

package org.broadleafcommerce.core.catalog;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuImpl;
import org.testng.annotations.DataProvider;

import java.math.BigDecimal;
import java.util.Calendar;

public class ProductDataProvider {

    /**
     * A basic product is actually a Product and a Sku
     */
    @DataProvider(name="basicProduct")
    public static Object[][] provideBasicProduct() {
        Product ci = new ProductImpl();
        
        Sku defaultSku = new SkuImpl();
        defaultSku.setName("setOfAggieDominoes");
        defaultSku.setDescription("a fine set of bones for 42");
        ci.setDefaultSku(defaultSku);

        return new Object[][]{{ci}};
    }

    @DataProvider(name="setupProducts")
    public static Object[][] createProducts() {
        Product p1 = getProduct(null);
        Product p2 = getProduct(null);
        Product p3 = getProduct(null);
        Product p4 = getProduct(null);
        Product p5 = getProduct(null);
        Product p6 = getProduct(null);
        Product p7 = getProduct(null);

        Object[][] objs = new Object[7][1];
        objs[0] = new Object[]{p1};
        objs[1] = new Object[]{p2};
        objs[2] = new Object[]{p3};
        objs[3] = new Object[]{p4};
        objs[4] = new Object[]{p5};
        objs[5] = new Object[]{p6};
        objs[6] = new Object[]{p7};

        return objs;
    }

    private static Product getProduct(Long id) {
        Calendar activeStartCal = Calendar.getInstance();
        activeStartCal.add(Calendar.DAY_OF_YEAR, -2);
        Product product = new ProductImpl();
        Sku defaultSku = new SkuImpl();
        defaultSku.setRetailPrice(new Money(BigDecimal.valueOf(15.0)));
        defaultSku.setSalePrice(new Money(BigDecimal.valueOf(10.0)));
        defaultSku.setActiveStartDate(activeStartCal.getTime());
        product.setDefaultSku(defaultSku);
        if (id == null) {
            defaultSku.setName("productNameTest");
            return product;
        }
        product.setId(id);
        defaultSku.setName(id.toString());
        defaultSku.setId(id);
        return product;
    }
}
