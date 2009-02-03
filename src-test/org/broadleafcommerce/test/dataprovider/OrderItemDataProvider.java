package org.broadleafcommerce.test.dataprovider;

import org.broadleafcommerce.order.domain.OrderItem;
import org.testng.annotations.DataProvider;

public class OrderItemDataProvider {

	@DataProvider(name="basicOrderItem")
	public static Object[][] provideBasicSalesOrderItem() {
		OrderItem soi = new OrderItem();
		soi.setFinalPrice(10.25);
		soi.setQuantity(3);
		return new Object[][]{{soi}};
	}
}
