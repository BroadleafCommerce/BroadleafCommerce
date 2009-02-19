package org.broadleafcommerce.order.domain;

import java.io.Serializable;

public class BroadleafFullfillmentGroupItem implements FullfillmentGroupItem,Serializable {

	private Long id;
	private Long fullfillmentGroupId;
	private OrderItem orderItem;
	private int quantity;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFullfillmentGroupId() {
		return fullfillmentGroupId;
	}
	public void setFullfillmentGroupId(Long fullfillmentGroupId) {
		this.fullfillmentGroupId = fullfillmentGroupId;
	}
	public OrderItem getOrderItem() {
		return orderItem;
	}
	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	

}
