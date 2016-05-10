/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
