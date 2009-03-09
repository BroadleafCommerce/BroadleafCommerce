package org.broadleafcommerce.pricing.service;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.FulfillmentGroupDao;
import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.springframework.stereotype.Service;

@Service("pricingService")
public class PricingServiceImpl implements PricingService {

    @Resource
    private OrderItemDao orderItemDao;

    @Resource
    private FulfillmentGroupDao fulfillmentGroupDao;

    @Override
    public Order calculateOrderAmount(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItemList = orderItemDao.readOrderItemsForOrder(order);
        for (OrderItem item : orderItemList) {
            total = total.add(item.getFinalPrice());
        }

        List<FulfillmentGroup> fulfillmentGroupList = fulfillmentGroupDao.readFulfillmentGroupsForOrder(order);
        for (FulfillmentGroup fulfillmentGroup : fulfillmentGroupList) {
            total = total.add(fulfillmentGroup.getCost());
        }
        order.setTotal(total);
        return order;
    }

}
