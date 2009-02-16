package org.broadleafcommerce.test.dataprovider;

import org.broadleafcommerce.catalog.domain.BroadleafSku;
import org.broadleafcommerce.catalog.domain.Sku;
import org.testng.annotations.DataProvider;

public class SkuDaoDataProvider {

    @DataProvider(name = "basicSku")
    public static Object[][] provideBasicSku() {
        Sku si = new BroadleafSku();
        return new Object[][] { { si } };
    }
}
