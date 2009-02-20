package org.broadleafcommerce.test.dataprovider;


import org.broadleafcommerce.order.domain.BroadleafOrder;
import org.testng.annotations.DataProvider;

public class OrderDataProvider {

	@DataProvider(name="basicOrder")
	public static Object[][] provideBasicSalesOrder() {
		BroadleafOrder so = new BroadleafOrder();
		so.setStatus("TEST ORDER STATUS");
		so.setTotalAmount(1000);
		return new Object[][]{{so}};
	}
	
}
