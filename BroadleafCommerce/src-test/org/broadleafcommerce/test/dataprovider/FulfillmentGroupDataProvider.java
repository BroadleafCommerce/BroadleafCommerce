package org.broadleafcommerce.test.dataprovider;

import org.broadleafcommerce.order.domain.FulfillmentGroupImpl;
import org.testng.annotations.DataProvider;

public class FulfillmentGroupDataProvider {

    @DataProvider(name = "basicFulfillmentGroup")
    public static Object[][] provideBasicSalesFulfillmentGroup() {
        FulfillmentGroupImpl sos = new FulfillmentGroupImpl();
        sos.setReferenceNumber("123456789");
        sos.setMethod("standard");
        return new Object[][] { { sos } };
    }
}
