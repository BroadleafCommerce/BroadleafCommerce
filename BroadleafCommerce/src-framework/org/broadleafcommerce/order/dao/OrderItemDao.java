package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.BroadleafOrder;
import org.broadleafcommerce.order.domain.OrderItem;

public interface OrderItemDao {

	public OrderItem readOrderItemById(Long orderItemId);
	
	public OrderItem maintainOrderItem(OrderItem orderItem);
	
	public void deleteOrderItem(OrderItem orderItem);
	
	public List<OrderItem> readOrderItemsForOrder(BroadleafOrder order);
}
