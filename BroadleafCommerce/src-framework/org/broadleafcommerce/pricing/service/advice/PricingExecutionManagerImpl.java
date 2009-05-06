package org.broadleafcommerce.pricing.service.advice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.order.domain.Order;
import org.springframework.stereotype.Component;

@Component("pricingExecutionManager")
public class PricingExecutionManagerImpl implements PricingExecutionManager, Compoundable<Order> {

    private static final Log LOG = LogFactory.getLog(PricingExecutionManagerImpl.class);

    private static final ThreadLocal <Order> uniqueOrder  = new ThreadLocal <Order>();

    public void clearCache() {
        uniqueOrder.remove();
    }

    public void executePricing(Order order) {
        uniqueOrder.set(order);
        LOG.debug("Context order reset : order id " + order.getId());
    }

    public Order getLatestItem() {
        Order order = uniqueOrder.get();
        if (order != null) {
            LOG.debug("Latest context order retrieved : order id " + order.getId());
        }

        return order;
    }
}
