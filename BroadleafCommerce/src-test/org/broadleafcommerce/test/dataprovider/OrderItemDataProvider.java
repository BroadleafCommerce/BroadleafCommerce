package org.broadleafcommerce.test.dataprovider;

import java.math.BigDecimal;

import org.broadleafcommerce.order.domain.BroadleafOrderItem;
import org.testng.annotations.DataProvider;

public class OrderItemDataProvider {

    @DataProvider(name = "basicOrderItem")
    public static Object[][] provideBasicSalesOrderItem() {
        BroadleafOrderItem soi = new BroadleafOrderItem();
        soi.setFinalPrice(BigDecimal.valueOf(10.25));
        soi.setQuantity(3);
        return new Object[][] { { soi } };
    }
}
