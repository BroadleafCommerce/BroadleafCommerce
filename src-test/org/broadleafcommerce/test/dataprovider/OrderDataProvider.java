package org.broadleafcommerce.test.dataprovider;

import java.math.BigDecimal;

import org.broadleafcommerce.order.domain.BroadleafOrder;
import org.testng.annotations.DataProvider;

public class OrderDataProvider {

    @DataProvider(name = "basicOrder")
    public static Object[][] provideBasicSalesOrder() {
        BroadleafOrder so = new BroadleafOrder();
        so.setStatus("TEST ORDER STATUS");
        so.setTotal(BigDecimal.valueOf(1000));
        return new Object[][] { { so } };
    }
}
