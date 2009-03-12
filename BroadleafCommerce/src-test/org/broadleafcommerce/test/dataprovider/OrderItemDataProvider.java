package org.broadleafcommerce.test.dataprovider;

import java.math.BigDecimal;

import org.broadleafcommerce.order.domain.OrderItemImpl;
import org.testng.annotations.DataProvider;

public class OrderItemDataProvider {

    @DataProvider(name = "basicOrderItem")
    public static Object[][] provideBasicSalesOrderItem() {
        OrderItemImpl soi = new OrderItemImpl();
        soi.setPrice(BigDecimal.valueOf(10.25));
        soi.setQuantity(3);
        return new Object[][] { { soi } };
    }
}
