package org.broadleafcommerce.test.dataprovider;

import java.math.BigDecimal;

import org.broadleafcommerce.order.domain.FullfillmentGroupImpl;
import org.testng.annotations.DataProvider;

public class FullfillmentGroupDataProvider {

    @DataProvider(name="basicFullfillmentGroup")
    public static Object[][] provideBasicSalesFullfillmentGroup(){
        FullfillmentGroupImpl sos = new FullfillmentGroupImpl();
        sos.setCost(BigDecimal.valueOf(9.99));
        sos.setReferenceNumber("123456789");
        sos.setMethod("UPS");
        return new Object[][]{{sos}};
    }
}
