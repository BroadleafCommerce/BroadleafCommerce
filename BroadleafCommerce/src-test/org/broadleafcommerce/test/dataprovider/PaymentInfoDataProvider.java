package org.broadleafcommerce.test.dataprovider;

import java.math.BigDecimal;

import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.order.domain.PaymentInfoImpl;
import org.broadleafcommerce.util.money.Money;
import org.testng.annotations.DataProvider;

public class PaymentInfoDataProvider {

    @DataProvider(name = "basicPaymentInfo")
    public static Object[][] provideBasicSalesPaymentInfo() {
        PaymentInfo sop = new PaymentInfoImpl();
        sop.setAmount(new Money(BigDecimal.valueOf(10.99)));
        sop.setReferenceNumber("987654321");
        return new Object[][] { { sop } };
    }
}
