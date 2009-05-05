package org.broadleafcommerce.pricing.service.advice;

import org.broadleafcommerce.order.domain.Order;

public interface PricingExecutionManager {

    public void executePricing(Order order);

}