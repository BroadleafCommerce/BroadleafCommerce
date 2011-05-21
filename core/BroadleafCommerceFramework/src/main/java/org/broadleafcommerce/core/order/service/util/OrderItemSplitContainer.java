package org.broadleafcommerce.core.order.service.util;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.core.order.domain.OrderItem;

public class OrderItemSplitContainer {
	
	protected OrderItem key;
	protected List<OrderItem> splitItems = new ArrayList<OrderItem>();
	
	public OrderItem getKey() {
		return key;
	}
	
	public void setKey(OrderItem key) {
		this.key = key;
	}
	
	public List<OrderItem> getSplitItems() {
		return splitItems;
	}
	
	public void setSplitItems(List<OrderItem> splitItems) {
		this.splitItems = splitItems;
	}

}
