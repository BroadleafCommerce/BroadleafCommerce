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
