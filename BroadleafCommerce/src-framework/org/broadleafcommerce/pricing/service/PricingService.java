package org.broadleafcommerce.pricing.service;

import org.broadleafcommerce.order.domain.Order;

public interface PricingService {

	Order calculateOrderTotal(Order order);
	
}
