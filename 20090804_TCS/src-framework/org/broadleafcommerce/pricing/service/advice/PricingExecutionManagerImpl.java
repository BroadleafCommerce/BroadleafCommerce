/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

@Component("blPricingExecutionManager")
public class PricingExecutionManagerImpl implements PricingExecutionManager, Ordered {

    private static final Log LOG = LogFactory.getLog(PricingExecutionManagerImpl.class);

    private static final ThreadLocal<Order> uniqueOrder = new ThreadLocal<Order>();

    private int order;

    @Resource(name = "blPricingService")
    private PricingService pricingService;

    @Resource(name = "blOrderDao")
    private OrderDao orderDao;

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
                /*
                 * We need to save the order before pricing, because it is
                 * sometimes possible for transient items to exist in the order
                 * hierarchy that will cause problems downstream during pricing.
                 */
                orderDao.save(orderItem);
                orderItem = pricingService.executePricing(orderItem);
                orderDao.save(orderItem);
                LOG.debug("Context order priced : order id " + orderItem.getId());
            }
        }

        return returnValue;
    }
}
