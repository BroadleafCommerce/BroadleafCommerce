package org.broadleafcommerce.test.dataprovider;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.domain.SkuImpl;
import org.testng.annotations.DataProvider;

public class SkuDaoDataProvider {

    @DataProvider(name = "basicSku")
    public static Object[][] provideBasicSku() {
        Sku si = new SkuImpl();
        return new Object[][] { { si } };
    }
}
