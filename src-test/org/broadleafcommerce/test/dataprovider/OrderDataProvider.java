package org.broadleafcommerce.test.dataprovider;


import org.broadleafcommerce.order.domain.Order;
import org.testng.annotations.DataProvider;

public class OrderDataProvider {

	@DataProvider(name="basicOrder")
	public static Object[][] provideBasicSalesOrder() {
		Order so = new Order();
		so.setOrderStatus("TEST ORDER STATUS");
		so.setOrderTotal(1000);
		return new Object[][]{{so}};
	}
	
}
