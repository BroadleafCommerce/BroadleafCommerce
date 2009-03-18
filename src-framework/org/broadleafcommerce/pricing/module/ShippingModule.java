package org.broadleafcommerce.pricing.module;

import org.broadleafcommerce.order.domain.FulfillmentGroup;

public interface ShippingModule {

	public String getName();
	
	public void setName(String name);
	
	public FulfillmentGroup calculateShippingForFulfillmentGroup(FulfillmentGroup fulfillmentGroup);
}
