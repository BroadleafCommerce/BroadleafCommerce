package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderItem;

public interface OrderItemDao {

	public OrderItem readOrderItemById(Long orderItemId);
	
	public OrderItem maintainOrderItem(OrderItem orderItem);
	
	public void deleteOrderItem(OrderItem orderItem);
	
//	public List<OrderItem> readOrderItemsForOrder(Order order);
//
//	public Order readOrderForOrderItem(OrderItem orderItem);
	
	public OrderItem create();
}
