package org.broadleafcommerce.test.dataprovider;

import org.broadleafcommerce.catalog.domain.Sku;
import org.testng.annotations.DataProvider;

public class SkuDaoDataProvider {

	@DataProvider(name="basicSku")
	public static Object[][] provideBasicSku(){
		Sku si = new Sku();
		return new Object[][]{{si}};
	}
	
}
