package org.broadleafcommerce.pricing.test;

import javax.annotation.Resource;

import org.broadleafcommerce.test.integration.BaseTest;
import org.broadleafcommerce.workflow.Processor;

public class PricingTest extends BaseTest {

    @Resource(name="pricingWorkflow")
    private Processor pricingWorkflow;

    //@Test
    /*public void testPricing() throws Exception {
		Order order = new OrderImpl();
		FulfillmentGroup group = new FulfillmentGroupImpl();
		List<FulfillmentGroup> groups = new ArrayList<FulfillmentGroup>();
		groups.add(group);
		order.setFulfillmentGroups(groups);
		Money total = new Money(5D);
		group.setPrice(total);
		order.setSubTotal(total);
		order.setTotal(total);
		pricingWorkflow.doActivities(order);

		assert (order.getTotal().greaterThan(order.getSubTotal()));
		assert (order.getTotalTax().equals(order.getSubTotal().multiply(0.05D)));
		assert (order.getTotal().equals(order.getSubTotal().add(order.getTotalTax())));
	}*/
}
