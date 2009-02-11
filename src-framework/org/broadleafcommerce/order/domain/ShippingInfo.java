package org.broadleafcommerce.order.domain;

import java.util.List;

import org.broadleafcommerce.profile.domain.Address;

public interface ShippingInfo {
	
	public Address getAddress();
	
	public void setAddress(Address address);
	
	public ShippingInfo addOrderItemToShippingInfo(OrderItem orderItem);
	
	public void setOrderItems(List<OrderItem> orderItems);
	
	public List<OrderItem> getOrderItems();
	
	public Object getShippingMethod();
	
	public void setShippingMethod(Object shippingMethod);
	
	public Object getShippingPrice();
	
	public void setShippingPrice(Object shippingPrice);
}
