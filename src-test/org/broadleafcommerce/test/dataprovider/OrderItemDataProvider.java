package org.broadleafcommerce.test.dataprovider;

import org.broadleafcommerce.order.domain.BroadleafOrderItem;
import org.testng.annotations.DataProvider;

public class OrderItemDataProvider {

	@DataProvider(name="basicOrderItem")
	public static Object[][] provideBasicSalesOrderItem() {
		BroadleafOrderItem soi = new BroadleafOrderItem();
		soi.setAmount(10.25);
		soi.setQuantity(3);
		return new Object[][]{{soi}};
	}
}
