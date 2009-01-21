package org.springcommerce.util;

import java.util.List;

import org.springcommerce.order.domain.OrderItem;

public class BasketItems {

	private List<OrderItem> items;

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
	
	public OrderItem getItemAt(int index){
		return items.get(index);
	}

	public void setItemAt(OrderItem item){
		
	}
	
	public void setItemAt(int index){
		
	}
	
	public void setItemAt(OrderItem item, int index){
		
	}
}
