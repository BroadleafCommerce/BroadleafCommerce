package org.broadleafcommerce.test.dataprovider;

import org.broadleafcommerce.order.domain.OrderPayment;
import org.testng.annotations.DataProvider;

public class OrderPaymentDataProvider {

	@DataProvider(name="basicOrderPayment")
	public static Object[][] provideBasicSalesOrderPayment() {
		OrderPayment sop = new OrderPayment();
		sop.setAmount(10.99);
		sop.setReferenceNumber("987654321");
		return new Object[][]{{sop}};
	}
}
