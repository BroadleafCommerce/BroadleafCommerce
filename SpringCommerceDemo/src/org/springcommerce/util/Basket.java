package org.springcommerce.util;

import java.util.List;

import org.springcommerce.order.domain.Order;
import org.springcommerce.order.domain.OrderItem;

public class Basket {

	private Order order;
	
	private List<OrderItem> items;

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
}
