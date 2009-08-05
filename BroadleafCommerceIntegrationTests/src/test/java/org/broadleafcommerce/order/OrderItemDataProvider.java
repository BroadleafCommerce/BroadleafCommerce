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
package org.broadleafcommerce.test.dataprovider;

import java.math.BigDecimal;

import org.broadleafcommerce.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.order.domain.GiftWrapOrderItemImpl;
import org.broadleafcommerce.order.domain.OrderItemImpl;
import org.broadleafcommerce.util.money.Money;
import org.testng.annotations.DataProvider;

public class OrderItemDataProvider {

    @DataProvider(name = "basicDiscreteOrderItem")
    public static Object[][] provideBasicDiscreteSalesOrderItem() {
        OrderItemImpl soi = new DiscreteOrderItemImpl();
        soi.setPrice(new Money(BigDecimal.valueOf(10.25)));
        soi.setQuantity(3);
        return new Object[][] { { soi } };
    }

    @DataProvider(name = "basicGiftWrapOrderItem")
    public static Object[][] provideBasicGiftWrapSalesOrderItem() {
        OrderItemImpl soi = new GiftWrapOrderItemImpl();
        soi.setPrice(new Money(BigDecimal.valueOf(1.25)));
        soi.setQuantity(1);
        return new Object[][] { { soi } };
    }
}
