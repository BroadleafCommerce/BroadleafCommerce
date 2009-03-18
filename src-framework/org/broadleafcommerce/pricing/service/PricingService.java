package org.broadleafcommerce.pricing.service;

import org.broadleafcommerce.order.domain.Order;

public interface PricingService {

	public Order calculateOrderTotal(Order order);
	
	public Order calculateShippingForOrder(Order order);
	
	public Order calculateTaxForOrder(Order order);
	
}
