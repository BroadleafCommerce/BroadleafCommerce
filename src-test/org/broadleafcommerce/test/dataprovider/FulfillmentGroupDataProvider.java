package org.broadleafcommerce.test.dataprovider;

import java.math.BigDecimal;

import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.testng.annotations.DataProvider;

public class FulfillmentGroupDataProvider {

    @DataProvider(name="basicFulfillmentGroup")
    public static Object[][] provideBasicSalesFulfillmentGroup(){
        FulfillmentGroupImpl sos = new FulfillmentGroupImpl();
        sos.setCost(BigDecimal.valueOf(9.99));
        sos.setReferenceNumber("123456789");
        sos.setMethod("UPS");
        return new Object[][]{{sos}};
    }
}
