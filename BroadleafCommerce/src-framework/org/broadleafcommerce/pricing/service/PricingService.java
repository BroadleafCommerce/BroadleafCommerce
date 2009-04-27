package org.broadleafcommerce.pricing.service;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.service.exception.PricingException;

public interface PricingService {

    public Order executePricing(Order order) throws PricingException;

}
