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
package org.broadleafcommerce.core.order;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItemImpl;
import org.broadleafcommerce.core.order.domain.OrderItemImpl;
import org.testng.annotations.DataProvider;

import java.math.BigDecimal;

public class OrderItemDataProvider {

    @DataProvider(name = "basicDiscreteOrderItem")
    public static Object[][] provideBasicDiscreteSalesOrderItem() {
        OrderItemImpl soi = new DiscreteOrderItemImpl();
        soi.setRetailPrice(new Money(BigDecimal.valueOf(10.25)));
        soi.setQuantity(3);
        return new Object[][] { { soi } };
    }

    @DataProvider(name = "basicGiftWrapOrderItem")
    public static Object[][] provideBasicGiftWrapSalesOrderItem() {
        OrderItemImpl soi = new GiftWrapOrderItemImpl();
        soi.setRetailPrice(new Money(BigDecimal.valueOf(1.25)));
        soi.setQuantity(1);
        return new Object[][] { { soi } };
    }
}
