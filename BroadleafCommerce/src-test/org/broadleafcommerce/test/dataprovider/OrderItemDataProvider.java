package org.broadleafcommerce.test.dataprovider;

import java.math.BigDecimal;

import org.broadleafcommerce.order.domain.DiscreteOrderItemImpl;
import org.broadleafcommerce.order.domain.OrderItemImpl;
import org.broadleafcommerce.util.money.Money;
import org.testng.annotations.DataProvider;

public class OrderItemDataProvider {

    @DataProvider(name = "basicOrderItem")
    public static Object[][] provideBasicSalesOrderItem() {
        OrderItemImpl soi = new DiscreteOrderItemImpl();
        soi.setPrice(new Money(BigDecimal.valueOf(10.25)));
        soi.setQuantity(3);
        return new Object[][] { { soi } };
    }
}
