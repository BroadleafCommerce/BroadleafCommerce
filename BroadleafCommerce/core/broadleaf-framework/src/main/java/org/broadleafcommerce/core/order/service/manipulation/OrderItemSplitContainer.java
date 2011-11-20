package org.broadleafcommerce.core.order.service.manipulation;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.core.offer.service.discount.domain.PromotableOrderItem;
import org.broadleafcommerce.core.order.domain.OrderItem;

public class OrderItemSplitContainer {
	
	protected OrderItem key;
	protected List<PromotableOrderItem> splitItems = new ArrayList<PromotableOrderItem>();
	
	public OrderItem getKey() {
		return key;
	}
	
	public void setKey(OrderItem key) {
		this.key = key;
	}
	
	public List<PromotableOrderItem> getSplitItems() {
		return splitItems;
	}
	
	public void setSplitItems(List<PromotableOrderItem> splitItems) {
		this.splitItems = splitItems;
	}

}
