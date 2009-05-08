package org.broadleafcommerce.pricing.service.advice;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.pricing.service.PricingService;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component("pricingExecutionManager")
public class PricingExecutionManagerImpl implements PricingExecutionManager, Ordered {

    private static final Log LOG = LogFactory.getLog(PricingExecutionManagerImpl.class);

    private static final ThreadLocal <Order> uniqueOrder  = new ThreadLocal <Order>();

    private int order;

    @Resource
    private PricingService pricingService;

    @Resource(name="orderDao")
    private OrderDao orderDao;

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

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

    public Object priceOrder(ProceedingJoinPoint call) throws Throwable {
        clearCache();
        Object returnValue;
        try {
            returnValue = call.proceed();
        } finally {
            Order orderItem = getLatestItem();
            clearCache();
            if (orderItem != null) {
                pricingService.executePricing(orderItem);
                orderDao.save(orderItem);
                LOG.debug("Context order priced : order id " + orderItem.getId());
            }
        }

        return returnValue;
    }
}
