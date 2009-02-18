package org.broadleafcommerce.test.dataprovider;

import org.broadleafcommerce.order.domain.BroadleafFullfillmentGroup;
import org.testng.annotations.DataProvider;

public class FullfillmentGroupDataProvider {
	
	@DataProvider(name="basicFullfillmentGroup")
	public static Object[][] provideBasicSalesFullfillmentGroup(){
		BroadleafFullfillmentGroup sos = new BroadleafFullfillmentGroup();
		sos.setCost(9.99);
		sos.setReferenceNumber("123456789");
		sos.setMethod("UPS");
		return new Object[][]{{sos}};
	}
}
