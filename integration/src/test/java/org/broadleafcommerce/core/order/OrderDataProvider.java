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
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.testng.annotations.DataProvider;

import java.math.BigDecimal;

public class OrderDataProvider {

    @DataProvider(name = "basicOrder")
    public static Object[][] provideBasicSalesOrder() {
        OrderImpl so = new OrderImpl();
        so.setStatus(OrderStatus.IN_PROCESS);
        so.setTotal(new Money(BigDecimal.valueOf(1000)));
        return new Object[][] { { so } };
    }
}
