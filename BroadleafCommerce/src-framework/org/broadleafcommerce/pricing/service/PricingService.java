package org.broadleafcommerce.pricing.service;

import org.broadleafcommerce.order.domain.Order;

public interface PricingService {

	Order calculateOrderAmount(Order order);
	
}
