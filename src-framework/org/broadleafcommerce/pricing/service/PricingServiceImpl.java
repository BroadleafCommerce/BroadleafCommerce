package org.broadleafcommerce.pricing.service;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.FulfillmentGroupDao;
import org.broadleafcommerce.order.dao.OrderItemDao;
import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.util.money.Money;
import org.springframework.stereotype.Service;

@Service("pricingService")
public class PricingServiceImpl implements PricingService {

    @Resource
    private OrderItemDao orderItemDao;

    @Resource
    private FulfillmentGroupDao fulfillmentGroupDao;

    @Override
    public Order calculateOrderTotal(Order order) {
        Money total = new Money(BigDecimal.ZERO);
        //List<OrderItem> orderItemList = orderItemDao.readOrderItemsForOrder(order);
        List<OrderItem> orderItemList = order.getOrderItems();
        for (OrderItem item : orderItemList) {
        	if(item.getPrice() == null){
        		item.setPrice(item.getSalePrice());
        	}
        	total = total.add(item.getPrice());        		
        }
        order.setSubTotal(total);

        //List<FulfillmentGroup> fulfillmentGroupList = fulfillmentGroupDao.readFulfillmentGroupsForOrder(order);
        List<FulfillmentGroup> fulfillmentGroupList = order.getFulfillmentGroups();
        for (FulfillmentGroup fulfillmentGroup : fulfillmentGroupList) {
            total = total.add(fulfillmentGroup.getRetailPrice());
        }
        order.setTotal(total);
        return order;
    }

	@Override
	public Order calculateShippingForOrder(Order order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order calculateTaxForOrder(Order order) {
		// TODO Auto-generated method stub
		return null;
	}

    
    
}
