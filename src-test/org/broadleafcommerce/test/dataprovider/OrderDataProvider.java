package org.broadleafcommerce.test.dataprovider;

import java.math.BigDecimal;

import org.broadleafcommerce.order.domain.OrderImpl;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.util.money.Money;
import org.testng.annotations.DataProvider;

public class OrderDataProvider {

    @DataProvider(name = "basicOrder")
    public static Object[][] provideBasicSalesOrder() {
        OrderImpl so = new OrderImpl();
        so.setStatus(OrderStatus.IN_PROCESS.toString());
        so.setTotal(new Money(BigDecimal.valueOf(1000)));
        return new Object[][] { { so } };
    }
}
