package org.broadleafcommerce.test.dataprovider;

import org.broadleafcommerce.catalog.domain.Product;
import org.testng.annotations.DataProvider;

public class ProductDataProvider {

    @DataProvider(name="basicProduct")
    public static Object[][] provideBasicProduct() {
        Product ci = new Product();
        ci.setName("setOfAggieDominoes");
        ci.setDescription("a fine set of bones for 42");
        return new Object[][]{{ci}};
    }
}
