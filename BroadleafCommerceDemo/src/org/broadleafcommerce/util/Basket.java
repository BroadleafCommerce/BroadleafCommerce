package org.broadleafcommerce.util;

import java.util.List;

import org.broadleafcommerce.order.domain.BroadleafOrder;
import org.broadleafcommerce.order.domain.OrderItem;

public class Basket {

	private BroadleafOrder order;
	
	private List<OrderItem> items;

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public BroadleafOrder getOrder() {
		return order;
	}

	public void setOrder(BroadleafOrder order) {
		this.order = order;
	}
	
}
