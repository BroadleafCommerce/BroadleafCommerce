package org.broadleafcommerce.test.dataprovider;

import org.broadleafcommerce.catalog.domain.SellableItem;
import org.testng.annotations.DataProvider;

public class SellableItemDaoDataProvider {

	@DataProvider(name="basicSellableItem")
	public static Object[][] provideBasicSellableItem(){
		SellableItem si = new SellableItem();
		return new Object[][]{{si}};
	}
	
}
