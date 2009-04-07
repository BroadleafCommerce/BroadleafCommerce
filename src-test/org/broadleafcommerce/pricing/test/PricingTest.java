package org.broadleafcommerce.pricing.test;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderImpl;
import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.workflow.Processor;
import org.testng.annotations.Test;

public class PricingTest extends BaseTest {

	@Resource(name="pricingWorkflow")
	private Processor pricingWorkflow;
	
	@Test
	public void testPricing() throws Exception {
		Order order = new OrderImpl();
		Money total = new Money(5D);
		order.setSubTotal(total);
		order.setTotal(total);
		pricingWorkflow.doActivities(order);
		
		assert (order.getTotal().greaterThan(order.getSubTotal()));
		assert (order.getTotal().equals(order.getSubTotal().multiply(1.05D)));
	}
}
