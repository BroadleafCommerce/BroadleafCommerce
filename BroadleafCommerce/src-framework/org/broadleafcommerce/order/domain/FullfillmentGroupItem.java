package org.broadleafcommerce.order.domain;

public interface FullfillmentGroupItem {

	public Long getId();
	
	public void setId(Long id);
	
	public Long getFullfillmentGroupId();
	
	public void setFullfillmentGroupId(Long fullfillmentGroupId);
	
	public OrderItem getOrderItem();
	
	public void setOrderItem(OrderItem orderItem);
	
	public int getQuantity();
	
	public void setQuantity(int quantity);
	
}
