package org.broadleafcommerce.pricing.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.FullfillmentGroupDao;
import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.domain.FullfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.springframework.stereotype.Service;

@Service("pricingService")
public class PricingServiceImpl implements PricingService {

	@Resource
	private OrderItemDao orderItemDao;
	
	@Resource
	private FullfillmentGroupDao fullfillmentGroupDao;
	
	@Override
	public Order calculateOrderAmount(Order order) {
        double total = 0;
        List<OrderItem> orderItemList = orderItemDao.readOrderItemsForOrder(order);
        for (OrderItem item : orderItemList) {
            total += item.getAmount();
        }

        List<FullfillmentGroup> fullfillmentGroupList = fullfillmentGroupDao.readFullfillmentGroupsForOrder(order);
        for (FullfillmentGroup fullfillmentGroup : fullfillmentGroupList) {
            total += fullfillmentGroup.getCost();
        }
        order.setTotalAmount(total);
        return order;
	}

}
