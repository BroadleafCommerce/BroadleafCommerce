package org.broadleafcommerce.test.dataprovider;


import org.broadleafcommerce.order.domain.BroadleafOrder;
import org.testng.annotations.DataProvider;

public class OrderDataProvider {

	@DataProvider(name="basicOrder")
	public static Object[][] provideBasicSalesOrder() {
		BroadleafOrder so = new BroadleafOrder();
		so.setOrderStatus("TEST ORDER STATUS");
		so.setOrderTotal(1000);
		return new Object[][]{{so}};
	}
	
}
