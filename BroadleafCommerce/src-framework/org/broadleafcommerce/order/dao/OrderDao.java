package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.BasketOrder;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.SubmittedOrder;
import org.broadleafcommerce.profile.domain.User;

public interface OrderDao {

	public Order readOrderById(Long orderId);
	
	public Order maintianOrder(Order order);
	
	public List<Order> readOrdersForUser(User user);
	
	public List<Order> readOrdersForUser(Long userId);
	
	public void deleteOrderForUser(Order order);
	
	public BasketOrder readBasketOrderForUser(User user);
	
	public SubmittedOrder submitOrder(Order basketOrder);
}
