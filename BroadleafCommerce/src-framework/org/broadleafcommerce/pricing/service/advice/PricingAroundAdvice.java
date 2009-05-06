package org.broadleafcommerce.pricing.service.advice;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.service.PricingService;
import org.springframework.core.Ordered;

public class PricingAroundAdvice implements Ordered {

    private static final Log LOG = LogFactory.getLog(PricingAroundAdvice.class);

    private int order;

    @Resource
    private Compoundable<Order> orderCompoundable;

    @Resource
    private PricingService pricingService;

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Object priceOrder(ProceedingJoinPoint call) throws Throwable {
        orderCompoundable.clearCache();
        Object returnValue;
        try {
            returnValue = call.proceed();
        } finally {
            Order orderItem = orderCompoundable.getLatestItem();
            orderCompoundable.clearCache();
            if (orderItem != null) {
                pricingService.executePricing(orderItem);
                LOG.debug("Context order priced : order id " + orderItem.getId());
            }
        }

        return returnValue;
    }
}
