package org.broadleafcommerce.test.dataprovider;

import org.broadleafcommerce.catalog.domain.CatalogItem;
import org.testng.annotations.DataProvider;

public class CatalogItemDataProvider {

    @DataProvider(name="basicCatalogItem")
    public static Object[][] provideBasicCatalogItem() {
        CatalogItem ci = new CatalogItem();
        ci.setName("setOfAggieDominoes");
        ci.setDescription("a fine set of bones for 42");
        return new Object[][]{{ci}};
    }
}
