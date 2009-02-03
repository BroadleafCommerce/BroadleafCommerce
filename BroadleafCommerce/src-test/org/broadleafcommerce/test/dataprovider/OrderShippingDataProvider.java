package org.broadleafcommerce.test.dataprovider;

import org.broadleafcommerce.order.domain.OrderShipping;
import org.testng.annotations.DataProvider;

public class OrderShippingDataProvider {
	
	@DataProvider(name="basicOrderShipping")
	public static Object[][] provideBasicSalesOrderShipping(){
		OrderShipping sos = new OrderShipping();
		sos.setCost(9.99);
		sos.setReferenceNumber("123456789");
		return new Object[][]{{sos}};
	}
}
