package org.broadleafcommerce.pricing.module;

import org.broadleafcommerce.order.domain.Order;

public interface TaxModule {
	
	public String getName();
	
	public void setName(String name);
	
	public Order calculateTaxForOrder(Order order);
	
}
